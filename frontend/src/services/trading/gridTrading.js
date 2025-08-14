// 网格交易相关纯函数与CSV解析

/**
 * 解析 CSV 文本为数组对象
 * 仅保留必须列：日期、收盘；其余列按原表头填充
 */
export function parseCSV(content) {
  try {
    const lines = content.split('\n').filter(line => line.trim())
    if (lines.length === 0) return []
    const headers = lines[0].split(',').map(h => h.replace(/"/g, '').trim())

    const data = []
    for (let i = 1; i < lines.length; i++) {
      const values = lines[i].split(',').map(v => v.replace(/"/g, '').trim())
      if (values.length >= 2 && values.length <= headers.length + 2) {
        const row = {}
        headers.forEach((header, index) => {
          row[header] = index < values.length ? values[index] : ''
        })
        if (row['日期'] && row['收盘']) {
          data.push(row)
        }
      }
    }
    return data
  } catch (error) {
    console.error('CSV解析错误:', error)
    throw error
  }
}

export function getBasePositionIndex(dates, params) {
  if (params.basePositionMode === 'days') {
    return Math.min(params.basePositionDays - 1, dates.length - 1)
  } else if (params.basePositionMode === 'date' && params.basePositionDate) {
    const targetDate = params.basePositionDate
    const index = dates.findIndex(date => date === targetDate)
    return index >= 0 ? index : -1
  }
  return -1
}

export function calculateSharpeRatio(profitHistory) {
  if (!profitHistory || profitHistory.length < 2) return '0.00'
  const returns = []
  for (let i = 1; i < profitHistory.length; i++) {
    const prev = profitHistory[i - 1].totalValue
    const cur = profitHistory[i].totalValue
    const dailyReturn = (cur - prev) / prev
    returns.push(dailyReturn)
  }
  const avgReturn = returns.reduce((a, b) => a + b, 0) / returns.length
  const variance = returns.reduce((a, b) => a + Math.pow(b - avgReturn, 2), 0) / returns.length
  const volatility = Math.sqrt(variance)
  const sharpeRatio = volatility > 0 ? (avgReturn / volatility) * Math.sqrt(252) : 0
  return sharpeRatio.toFixed(2)
}

/**
 * 网格交易主算法（纯函数）
 * @param {Array} data 解析后的CSV数据
 * @param {Object} params 策略参数
 * @param {Object} moduleStates 模块开关（riskControl, advancedStrategy, marketEnvironment, fundManagement）
 */
export function calculateGridTrading(data, params, moduleStates) {
  const initialCapital = params.initialCapital * 10000 // 元

  // 获取价格与日期（最新在后）
  const prices = data.map(row => parseFloat(row['收盘']) || 0).reverse()
  const dates = data.map(row => row['日期']).reverse()

  if (prices.length === 0) {
    throw new Error('价格数据为空')
  }

  // 建仓索引与基准价
  const basePositionIndex = getBasePositionIndex(dates, params)
  let gridCenterPrice
  if (basePositionIndex >= 0 && basePositionIndex < prices.length) {
    gridCenterPrice = prices[basePositionIndex]
  } else {
    gridCenterPrice = prices[0]
    console.warn('使用首日价格作为网格基准点')
  }

  // 网格间距
  let gridStep
  if (params.gridWidthMode === 'percentage') {
    gridStep = gridCenterPrice * params.gridDensity / 100
  } else {
    gridStep = params.gridWidth
  }

  // 生成网格线
  const gridLines = []
  const halfLevels = Math.floor(params.gridLevels / 2)
  for (let i = halfLevels; i >= 0; i--) {
    gridLines.push(gridCenterPrice - i * gridStep)
  }
  for (let i = 1; i <= halfLevels; i++) {
    gridLines.push(gridCenterPrice + i * gridStep)
  }
  gridLines.sort((a, b) => a - b)

  // 运行时变量
  let capital = initialCapital
  let position = 0
  let totalProfit = 0
  let tradeCount = 0
  let winCount = 0
  const profitHistory = []
  const drawdownHistory = []
  const tradeHistory = []
  const allocationHistory = []
  let maxCapital = initialCapital
  const buyPrices = []
  let consecutiveDownDays = 0

  for (let i = 0; i < prices.length; i++) {
    const currentPrice = prices[i]
    const currentDate = dates[i]
    let traded = false

    // 底仓
    if (i === basePositionIndex && params.basePositionRatio > 0) {
      const baseAmount = initialCapital * params.basePositionRatio / 100
      const shares = baseAmount / currentPrice
      const feeRate = moduleStates?.riskControl ? params.feeRate : 0
      const fee = baseAmount * feeRate / 100

      capital -= (baseAmount + fee)
      position += shares
      tradeCount++
      tradeHistory.push({
        date: currentDate,
        type: 'buy',
        price: currentPrice,
        shares,
        amount: baseAmount,
        fee,
        capital,
        position,
        reason: '底仓建立'
      })
      buyPrices.push(currentPrice)
      traded = true
    }

    // 连续下跌（熊市保护）
    if (i > 0 && moduleStates?.riskControl && params.bearMarketProtection) {
      if (prices[i] < prices[i - 1]) {
        consecutiveDownDays++
      } else {
        consecutiveDownDays = 0
      }
    }

    // 网格交易
    if (i > 0 && !traded) {
      const lastPrice = prices[i - 1]
      for (let j = 0; j < gridLines.length - 1; j++) {
        const lowerGrid = gridLines[j]
        const upperGrid = gridLines[j + 1]

        // 下穿买入
        if (lastPrice > lowerGrid && currentPrice <= lowerGrid) {
          const shouldBuy = (!moduleStates?.riskControl || !params.bearMarketProtection) || consecutiveDownDays < params.bearMarketDays
          if (shouldBuy && capital > 0) {
            const currentPositionValue = position * currentPrice
            const totalAssets = capital + currentPositionValue
            const positionRatio = currentPositionValue / totalAssets * 100
            if (positionRatio < params.maxPositionRatio) {
              const buyAmount = Math.min(
                capital * params.singleTradeRatio / 100,
                capital * 0.9
              )
              if (buyAmount > 100) {
                const shares = buyAmount / currentPrice
                const feeRate = moduleStates?.riskControl ? params.feeRate : 0
                const fee = buyAmount * feeRate / 100
                capital -= (buyAmount + fee)
                position += shares
                tradeCount++
                tradeHistory.push({
                  date: currentDate,
                  type: 'buy',
                  price: currentPrice,
                  shares,
                  amount: buyAmount,
                  fee,
                  capital,
                  position,
                  reason: `网格买入 (${lowerGrid.toFixed(2)})`
                })
                buyPrices.push(currentPrice)
                break
              }
            }
          }
        }

        // 上穿卖出
        if (lastPrice < upperGrid && currentPrice >= upperGrid && position > 0) {
          const sellShares = position * params.singleTradeRatio / 100
          if (sellShares >= 1) {
            const sellAmount = sellShares * currentPrice
            const feeRate = moduleStates?.riskControl ? params.feeRate : 0
            const fee = sellAmount * feeRate / 100
            capital += (sellAmount - fee)
            position -= sellShares
            tradeCount++
            if (buyPrices.length > 0) {
              const avgBuyPrice = buyPrices.reduce((a, b) => a + b, 0) / buyPrices.length
              if (currentPrice > avgBuyPrice) {
                winCount++
              }
            }
            tradeHistory.push({
              date: currentDate,
              type: 'sell',
              price: currentPrice,
              shares: sellShares,
              amount: sellAmount,
              fee,
              capital,
              position,
              reason: `网格卖出 (${upperGrid.toFixed(2)})`
            })
            break
          }
        }
      }
    }

    // 资产、收益与回撤
    const currentValue = capital + position * currentPrice
    const profit = currentValue - initialCapital

    // 止损/止盈日志（仅提示位，未强制平仓）
    if (moduleStates?.riskControl) {
      if (params.stopLossRatio > 0 && profit < -initialCapital * params.stopLossRatio / 100) {
        console.log('触发止损:', profit, -initialCapital * params.stopLossRatio / 100)
      }
      if (params.takeProfitRatio > 0 && profit > initialCapital * params.takeProfitRatio / 100) {
        console.log('触发止盈:', profit, initialCapital * params.takeProfitRatio / 100)
      }
    }

    profitHistory.push({
      date: currentDate,
      profit,
      totalValue: currentValue,
      profitRatio: (profit / initialCapital * 100).toFixed(2)
    })

    if (currentValue > maxCapital) {
      maxCapital = currentValue
    }
    const drawdown = (maxCapital - currentValue) / maxCapital * 100
    drawdownHistory.push({ date: currentDate, drawdown })

    allocationHistory.push({
      date: currentDate,
      capital,
      position: position * currentPrice,
      total: currentValue
    })

    totalProfit = profit
  }

  // 统计指标
  const days = prices.length
  const years = days / 365
  const annualReturn = years > 0 ? ((totalProfit + initialCapital) / initialCapital - 1) / years * 100 : 0
  const maxDrawdown = Math.max(...drawdownHistory.map(d => d.drawdown))
  const winRate = tradeCount > 0 ? (winCount / tradeCount * 100).toFixed(2) : '0.00'
  const sharpeRatio = calculateSharpeRatio(profitHistory)

  const maxPrice = Math.max(...prices)
  const minPrice = Math.min(...prices)

  return {
    annualReturn: annualReturn.toFixed(2),
    totalProfit: totalProfit.toFixed(2),
    maxDrawdown: maxDrawdown.toFixed(2),
    tradeCount,
    winRate,
    sharpeRatio,
    basePositionPrice: basePositionIndex >= 0 ? prices[basePositionIndex].toFixed(2) : '未建仓',
    basePositionDate: basePositionIndex >= 0 ? dates[basePositionIndex] : '',
    gridCenterPrice: gridCenterPrice.toFixed(2),
    periodHighPrice: maxPrice.toFixed(2),
    periodLowPrice: minPrice.toFixed(2),
    gridStep: gridStep.toFixed(2),
    gridRange: `${gridLines[0].toFixed(2)} - ${gridLines[gridLines.length - 1].toFixed(2)}`,
    profitHistory,
    drawdownHistory,
    tradeHistory,
    allocationHistory,
    gridLines,
    prices,
    dates
  }
}


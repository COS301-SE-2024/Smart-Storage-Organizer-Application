
act = require '../src/act.coffee'

describe 'act.events', ->

  it 'on exists', ->
    expect(act.on).to.be.a 'function'

  it 'off exists', ->
    expect(act.off).to.be.a 'function'

  it 'trigger exists', ->
    expect(act.trigger).to.be.a 'function'

  it 'on render called every tick', (done) ->
    act.renderNextTick()
    stop = act.on 'render', ->
      stop()
      done()

  it 'on custom', (done) ->
    stop = act.on 'custom', ->
        done()
    act.trigger 'custom'
    stop()
    act.trigger 'custom'

  it 'on custom (multiple events)', (done) ->
    expectedCalls = {custom1: 1, custom2: 1}
    stop = act.on 'custom1 custom2', (eventName) ->
      throw new Error("Unknown event: #{eventName}") if _.isUndefined expectedCalls[eventName]
      throw new Error("Event called too much: #{eventName}") if expectedCalls[eventName] == 0
      expectedCalls[eventName]--
      for k, v of expectedCalls
        return if v != 0
      done()
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    stop()
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

  it 'on custom with manual off (multiple events)', (done) ->
    expectedCalls = {custom1: 1, custom2: 2}
    stop = act.on 'custom1 custom2', (eventName) ->
      throw new Error("Unknown event: #{eventName}") if _.isUndefined expectedCalls[eventName]
      throw new Error("Event called too much: #{eventName}") if expectedCalls[eventName] == 0
      expectedCalls[eventName]--
      for k, v of expectedCalls
        return if v != 0
      done()
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom1'
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom2'
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

  it 'on custom remove by callback', (done) ->
    expectedCalls = {custom1: 1, custom2: 2}
    stop = act.on 'custom1 custom2', (eventName) ->
      throw new Error("Unknown event: #{eventName}") if _.isUndefined expectedCalls[eventName]
      throw new Error("Event called too much: #{eventName}") if expectedCalls[eventName] == 0
      expectedCalls[eventName]--
      for k, v of expectedCalls
        return if v != 0
      done()
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom1'
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom2'
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

  it 'on custom with context', (done) ->
    ctx = local: 1
    stop = act.on 'custom', ((eventName) ->
      expect(this).to.equal ctx
      done()), ctx
    act.trigger 'custom', 'custom'

    act.off 'custom'
    act.trigger 'custom', 'custom'

  it 'on custom remove by context', (done) ->
    expectedCalls = {custom1: 1, custom2: 1}
    ctx = local: 1
    stop = act.on 'custom1 custom2', ((eventName) ->
      throw new Error("Unknown event: #{eventName}") if _.isUndefined expectedCalls[eventName]
      throw new Error("Event called too much: #{eventName}") if expectedCalls[eventName] == 0
      expect(this).to.equal ctx
      expectedCalls[eventName]--
      for k, v of expectedCalls
        return if v != 0
      done()), ctx
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

    # Remove all with specific context
    act.off null, null, ctx

    # Future triggers don't have effect
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

  it 'on custom remove by name and context', (done) ->
    expectedCalls = {custom1: 5, custom2: 3}
    ctx1 = calls: 4
    ctx2 = calls: 4

    done_ = ->
      expect(ctx1.calls).to.equal 0
      expect(ctx2.calls).to.equal 0
      done()

    fn = (eventName) ->
      throw new Error("Unknown event: #{eventName}") if _.isUndefined expectedCalls[eventName]
      throw new Error("Event called too much: #{eventName}") if expectedCalls[eventName] == 0
      throw new Error("Context passed too many times") if @calls == 0
      expectedCalls[eventName]--
      @calls--
      for k, v of expectedCalls
        return if v != 0
      done_()

    act.on 'custom1', fn, ctx1
    act.on 'custom2', fn, ctx1
    act.on 'custom1', fn, ctx2

    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom1', null, ctx1
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom2', null, ctx2
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom2', null, ctx1
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'
    act.off 'custom1', null, ctx2
    act.trigger 'custom1', 'custom1'
    act.trigger 'custom2', 'custom2'

  it 'on custom list', (done) ->
    done()




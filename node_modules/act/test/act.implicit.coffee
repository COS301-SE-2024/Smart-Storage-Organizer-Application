
act = require '../src/act.coffee'

describe 'act.implicit', ->

  it 'parallel one property', ->
    point = x:0
    _point = act.implicit point

    _point.x = 1

    act.fastForward 0
    expect(_point.x).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 0.5

    act.fastForward +0.5
    expect(_point.x).to.equal 1

  it 'parallel two properties', ->
    point = x:0, y:0
    _point = act.implicit point

    _point.x = 1
    _point.y = 2

    expect(_point.x).to.equal 0
    expect(_point.y).to.equal 0

    act.fastForward 0
    expect(_point.x).to.equal 0
    expect(_point.y).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 0.5
    expect(_point.y).to.equal 1

    act.fastForward +0.5
    expect(_point.x).to.equal 1
    expect(_point.y).to.equal 2

  it 'serial two properties', ->
    point = x:0, y:0
    _point = act.implicit point

    act.begin(serial: true)
    _point.x = 1
    _point.y = 2
    act.commit()

    expect(_point.x).to.equal 0
    expect(_point.y).to.equal 0

    act.fastForward 0
    expect(_point.x).to.equal 0
    expect(_point.y).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 0.5
    expect(_point.y).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 1
    expect(_point.y).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 1
    expect(_point.y).to.equal 1

    act.fastForward +0.5
    expect(_point.x).to.equal 1
    expect(_point.y).to.equal 2

  it 'parallel one property with rate 2', ->
    point = x:0
    _point = act.implicit point

    act.begin(rate: 2)
    _point.x = 1
    act.commit()

    act.fastForward 0
    expect(_point.x).to.equal 0

    act.fastForward +0.25
    expect(_point.x).to.equal 0.5

    act.fastForward +0.25
    expect(_point.x).to.equal 1

    act.fastForward +1
    expect(_point.x).to.equal 1

  it 'explicit animation', ->
    point = x:0
    _point = act.implicit point

    act _point, x:'+=':1
    expect(_point.x).to.equal 0

    act.fastForward 0
    expect(_point.x).to.equal 0

    act.fastForward +0.5
    expect(_point.x).to.equal 0.5

    act.fastForward +0.5
    expect(_point.x).to.equal 1

  linearAnimator = (obj, destinations) ->
    _interp = (a, b, t) -> a * (1.0 - t) + b * t
    initials = _.pick obj, (_.keys destinations)...
    finals = _.clone destinations
    (t) ->
      changed = {}
      for k, v of finals
        changed[k] = _interp initials[k], finals[k], t
      changed

  # Animates one key at a time linearly in alphabetical older (like Manhattan distance!)
  manhattanAnimator = (obj, destination) ->
    animators = []
    keys = (_.keys destination).sort()
    for k in keys
      animators.push linearAnimator obj, (_.pick destination, k)
    (t) ->
      ans = {}
      t *= keys.length
      for animator in animators
        t1 = if t < 0 then 0 else if t > 1 then 1 else t
        _.extend ans, animator t1
        t -= 1
      ans

  it 'custom inline animator', ->
    point = x:0, y:0
    act point, {x:1, y:2}, {animator: manhattanAnimator}

    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward 0
    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 2

  it 'actOptions', ->
    point = x:0, name: 'Evan', actOptions: name: 'setAtEnd'
    _point = act.implicit point

    _point.name = "James"
    expect(_point.name).to.equal "Evan"

    act.fastForward 0
    expect(_point.name).to.equal "Evan"

    act.fastForward +0.5
    expect(_point.name).to.equal "Evan"

    act.fastForward +0.5
    expect(_point.name).to.equal "James"

  it 'actOptions can omit values with null', ->
    point = x:0, name: 'Evan', actOptions: name: 'setAtEnd', x: null
    _point = act.implicit point
    _point.x = 1
    expect(_point.x).to.equal 1

    act.fastForward 0
    expect(_point.x).to.equal 1

    act.fastForward +0.5
    expect(_point.x).to.equal 1

    act.fastForward +0.5
    expect(_point.x).to.equal 1


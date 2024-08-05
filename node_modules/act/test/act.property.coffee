
act = require '../src/act.coffee'

describe 'act.property', ->

  it 'parallel one property', ->

    point = {}
    act.property point, 'x', 0

    point.x = 1

    act.fastForward 0
    expect(point.x).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 0.5

    act.fastForward +0.5
    expect(point.x).to.equal 1

  it 'parallel one property with options', ->

    point = {}
    act.property point, 'x', 0, duration: 2

    point.x = 1
    expect(point.x).to.equal 0

    act.fastForward 0
    expect(point.x).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 0.25

    act.fastForward +0.5
    expect(point.x).to.equal 0.5

    act.fastForward +0.5
    expect(point.x).to.equal 0.75

    act.fastForward +0.5
    expect(point.x).to.equal 1

  it 'parallel one property with actOptions', ->

    point = {actOptions: x: duration: 2}
    act.property point, 'x', 0

    point.x = 1
    expect(point.x).to.equal 0

    act.fastForward 0
    expect(point.x).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 0.25

    act.fastForward +0.5
    expect(point.x).to.equal 0.5

    act.fastForward +0.5
    expect(point.x).to.equal 0.75

    act.fastForward +0.5
    expect(point.x).to.equal 1

  it 'parallel two properties (with duration)', ->
    point = {z:0}
    act.properties point, {x:0, y:0}, x:duration:2

    point.x = 1
    point.y = 2
    point.z = 3

    expect(point.x).to.equal 0
    expect(point.y).to.equal 0
    expect(point.z).to.equal 3

    act.fastForward 0
    expect(point.x).to.equal 0
    expect(point.y).to.equal 0
    expect(point.z).to.equal 3

    act.fastForward +0.5
    expect(point.x).to.equal 0.25
    expect(point.y).to.equal 1

    act.fastForward +0.5
    expect(point.x).to.equal 0.5
    expect(point.y).to.equal 2
    expect(point.z).to.equal 3

    act.fastForward +0.5
    expect(point.x).to.equal 0.75
    expect(point.y).to.equal 2

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 2
    expect(point.z).to.equal 3

  it 'parallel two properties (with actOptions)', ->
    point = {z:0, actOptions: x:duration:2}
    act.properties point, {x:0, y:0}

    point.x = 1
    point.y = 2
    point.z = 3

    expect(point.x).to.equal 0
    expect(point.y).to.equal 0
    expect(point.z).to.equal 3

    act.fastForward 0
    expect(point.x).to.equal 0
    expect(point.y).to.equal 0
    expect(point.z).to.equal 3

    act.fastForward +0.5
    expect(point.x).to.equal 0.25
    expect(point.y).to.equal 1

    act.fastForward +0.5
    expect(point.x).to.equal 0.5
    expect(point.y).to.equal 2
    expect(point.z).to.equal 3

    act.fastForward +0.5
    expect(point.x).to.equal 0.75
    expect(point.y).to.equal 2

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 2
    expect(point.z).to.equal 3

  it 'serial two properties', ->
    point = {}
    act.properties point, {x:0, y:0}

    act.begin(serial: true)
    point.x = 1
    point.y = 2
    act.commit()

    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward 0
    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 0.5
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 1

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 2

  it 'act options ', ->
    point = {}
    act.properties point, {x:0, y:0}

    act.begin(serial: true)
    point.x = 1
    point.y = 2
    act.commit()

    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward 0
    expect(point.x).to.equal 0
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 0.5
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 0

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 1

    act.fastForward +0.5
    expect(point.x).to.equal 1
    expect(point.y).to.equal 2


act = require '../src/act.coffee'

describe 'act.transaction', ->

  it 'value transaction (=)', ->
    point = x:0, y:0
    act.begin()
    act point, x: 1, y:2
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0
    point.y.should.equal 0

    act.fastForward(0.5)
    point.x.should.equal 0.5
    point.y.should.equal 1

    act.fastForward(0.5)
    point.x.should.equal 1
    point.y.should.equal 2

  it 'value transaction (+=)', ->
    point = x:0, y:0
    act.begin()
    act point, x: '+=': 1
    act point, y: '+=': 2
    act.commit()

    act.fastForward(0.5)
    point.x.should.equal 0.5
    point.y.should.equal 1

    act.fastForward(0.5)
    point.x.should.equal 1
    point.y.should.equal 2

  it 'value transaction (-=)', ->
    point = x:0, y:0
    act.begin()
    act point, x: '-=': 1
    act point, y: '-=': 2
    act.commit()

    act.fastForward(0.5)
    point.x.should.equal -0.5
    point.y.should.equal -1

    act.fastForward(0.5)
    point.x.should.equal -1
    point.y.should.equal -2

  it 'value transaction (*=)', ->
    point = x:1, y:1
    act.begin()
    act point, x: '*=': 2
    act point, y: '*=': 3
    act.commit()

    act.fastForward(0.5)
    point.x.should.equal 1.5
    point.y.should.equal 2

    act.fastForward(0.5)
    point.x.should.equal 2
    point.y.should.equal 3

  it 'value transaction (/=)', ->
    point = x:1, y:1
    act.begin()
    act point, x: '/=': 2
    act point, y: '/=': 3
    act.commit()

    act.fastForward(0.5)
    point.x.should.equal (1+1/2)/2
    point.y.should.equal (1+1/3)/2

    act.fastForward(0.5)
    point.x.should.equal 1/2
    point.y.should.equal 1/3

  it 'value transaction (rate 2)', ->
    point = x:0
    act.begin(rate: 2) # two times faster
    act point, x: 1
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.25)
    point.x.should.equal 0.5

    act.fastForward(0.25)
    point.x.should.equal 1

    act.fastForward(1)
    point.x.should.equal 1

  it 'value transaction (rate 1/2)', ->
    point = x: 0
    act.begin(rate: 1/2) # half as fast
    act point, x: 1
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(1)
    point.x.should.equal 0.5

    act.fastForward(1)
    point.x.should.equal 1

  # throw on duration 0
  it 'duration 0', ->
    point = x:0, y:0
    expect(-> act point, x:1, {duration: 0}).to.throw 'act: duration must be positive'

  # throw on rate 0
  it 'rate 0', ->
    point = x:0, y:0
    expect(->
      act.begin(rate:0)
      act(point, x:1)
      act.commit()
    ).to.throw 'act: rate must be positive'

  it 'serial transaction', ->
    point = x: 0, y: 0
    act.begin(serial: true)
    act point, x: 1
    act point, x: 2
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.5)
    point.x.should.equal 0.5

    act.fastForward(0.5)
    point.x.should.equal 1

    act.fastForward(0.5)
    point.x.should.equal 1.5

    act.fastForward(0.5)
    point.x.should.equal 2

  it 'serial transaction (multiple properties)', ->
    point = x: 0, y: 0
    act.begin(serial: true)
    act point, x: 1
    act point, y: 2
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0
    point.y.should.equal 0

    act.fastForward(1)
    point.x.should.equal 1
    point.y.should.equal 0

    act.fastForward(2)
    point.x.should.equal 1
    point.y.should.equal 2

  it 'parallel (EaseIn)', ->

    point = x: 0
    act.begin(easing: act.EaseIn)
    act point, x: 1
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.5)
    point.x.should.be.within(0.01, 0.49)

    act.fastForward(0.5)
    point.x.should.equal 1

  it 'parallel (EaseOut)', ->

    point = x: 0
    act.begin(easing: act.EaseOut)
    act point, x: 1
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.5)
    point.x.should.be.within(0.75, 0.99)

    act.fastForward(0.5)
    point.x.should.equal 1

  it 'parallel (EaseInOut)', ->

    point = x: 0
    act.begin(easing: act.EaseInOut)
    act point, x: 1
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.25)
    point.x.should.be.within(0.01, 0.24)

    act.fastForward(0.25)
    point.x.should.equal 0.5

    act.fastForward(0.25)
    point.x.should.be.within(0.75, 0.99)

    act.fastForward(0.25)
    point.x.should.equal 1


  it 'task with duration', ->
    point = x: 0
    act.begin()
    act point, x: 1, {duration: 2}
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.5)
    point.x.should.equal 0.25

    act.fastForward(0.5)
    point.x.should.equal 0.5

    act.fastForward(0.5)
    point.x.should.equal 0.75

    act.fastForward(0.5)
    point.x.should.equal 1


  it 'task with duration (serial, EaseInOut)', ->
    point = x: 0
    act.begin(serial: true)
    act point, x: 1, {duration: 2, easing: act.EaseInOut}
    act point, x: 2, {duration: 2, easing: act.EaseInOut}
    act.commit()

    act.fastForward(0)
    point.x.should.equal 0

    act.fastForward(0.5)
    point.x.should.be.within(0.01, 0.24)

    act.fastForward(0.5)
    point.x.should.equal 0.5

    act.fastForward(0.5)
    point.x.should.be.within(0.76, 0.99)

    act.fastForward(0.5)
    point.x.should.equal 1

    act.fastForward(0.5)
    point.x.should.be.within(1.01, 1.25)

    act.fastForward(0.5)
    point.x.should.equal 1.5

    act.fastForward(0.5)
    point.x.should.be.within(1.76, 1.99)

    act.fastForward(0.5)
    point.x.should.equal 2

  it 'setTimeout starts by default', (done) ->
    point = x: 0
    act point, x: 1, {duration: 0.015}
    setTimeout (->
      point.x.should.equal 1
      done()
    ), 0.02 * 1000

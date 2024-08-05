
# act
# ====================================================================
# Transaction based animation for dom and canvas
#
# ### Usage
#
# #### act
#

_ = require 'underscore'

# act.function
# ---------------------------------------------------------------------

_actInstanceID = 1

actGenerator = ->

  # act.function
  # ---------------------------------------------------------------------
  act = (obj, dest, options = {}) ->
    scheduler = act._scheduler

    if obj instanceof Implicit
      obj = obj._obj

    if options.animator
      task = new Task obj, dest, act, options
      scheduler.addTask task
    else
      for k of dest
        task = new Task obj, (_.pick dest, k), act, options
        scheduler.addTask task

  # For debugging, mark each instance if act with a unique ID
  act.instanceID = _actInstanceID
  _actInstanceID++

  # act.EaseLinear, etc.
  # ---------------------------------------------------------------------

  for k, v of Ease
    act[k] = v

  act.animator = (name) -> mapAnimatorFromName[name]

  # act.on, act.off, act.trigger
  # ---------------------------------------------------------------------

  _extendEventFunctions act

  # act.clone
  # ---------------------------------------------------------------------
  # Create a seperate act function with seperate scheduler, rate, etc
  # This does not clone tasks.

  act.clone = actGenerator

  # act.rate
  # ---------------------------------------------------------------------
  # Rate allows speeding up and slowing down time.
  #
  #     rate = 2        Twice as fast
  #     rate = 0.5      Half as fast
  #

  act.rate = 1

  # act.tickInterval
  # ---------------------------------------------------------------------
  # Interval in seconds between update ticks (default: 1/60, 60 Hz)
  act.tickInterval = 1/60

  # act._transactionBuilders
  # ---------------------------------------------------------------------

  act._transactionBuilders = []

  # act.play
  # ---------------------------------------------------------------------

  act.play = ->
    act._forceRenderNextTick = true
    if not act._playState
      # Store reference to state to indentity specific start has been stopped
      act._playState = state = lastTick: _getTime()
      ticker = ->
        if act._playState == state
          # Set up next tick
          _setTimeout act.tickInterval, ticker
          # Step all tasks
          timeCurrent = _getTime()
          timeElapsed = timeCurrent - state.lastTick
          changed = act._tick timeElapsed * act.rate
          state.lastTick = timeCurrent
          # If any task stepped, render
          if changed or act._forceRenderNextTick
            act._forceRenderNextTick = false
            act.trigger 'render'
      # Start tick
      _setTimeout act.tickInterval, ticker

  # act._tick
  # ---------------------------------------------------------------------
  # Tick all your tasks

  act._tick = (dt) ->
    act._rootScheduler.tick dt

  # act.fastForward
  # ---------------------------------------------------------------------
  # Fast forward in time

  act.fastForward = act._tick

  # act.stop
  # ---------------------------------------------------------------------

  act.stop = ->
    act._playState = null

  # act.begin
  # ---------------------------------------------------------------------
  # Start defining a new transaction
  act.begin = (options) ->
    builder = new TransactionBuilder options
    act._transactionBuilders.push builder
    builder

  # act._scheduler
  # ---------------------------------------------------------------------
  Object.defineProperty act, '_scheduler',
    get: ->
      len = act._transactionBuilders.length
      if len then act._transactionBuilders[len-1] else act._rootScheduler
    enumerable: false

  # act.commit
  # ---------------------------------------------------------------------
  # Stop defining a transaction and add it to its parent transaction
  act.commit = () ->
    builderTop = act._transactionBuilders.pop()
    scheduler = @_scheduler
    scheduler.addTask builderTop.transaction()

  act.renderNextTick = ->
    act._forceRenderNextTick = true

  # act.property
  # ---------------------------------------------------------------------

  act.property = (obj, key, val, options) ->
    # Default options to actOptions[k]
    if (_.isUndefined options) and obj.actOptions
      options = if _.isFunction obj.actOptions
          obj.actOptions()[key]
        else
          obj.actOptions[key]
    # Close over animatable value
    store = value: val

    setter = (v) -> act store, value: v, options
    setter._actStore = store

    Object.defineProperty obj, key,
      get: -> store.value
      set: setter

  act._implicitPropertyStore = (obj, key) ->
    d = Object.getOwnPropertyDescriptor obj, key
    return d?.set?._actStore

  # act.properties
  # ---------------------------------------------------------------------

  act.properties = (obj, properties, options) ->
    for k, v of properties
      act.property obj, k, v, options?[k]

  # act.implicit
  # ---------------------------------------------------------------------

  act.implicit = (obj, options = {}) ->
    # Default animation options
    options = _.clone options
    if obj.actOptions
      _.defaults options, (if _.isFunction obj.actOptions then obj.actOptions() else obj.actOptions)
    for k, v of obj
      # Null means omit
      if options[k] == null
        options[k] = {animator: null}
      # Undefined means default animator
      else if options[k] == undefined
        options[k] = {animator: _defaultInterpolationForValue v}
      else if _.isString options[k] or _.isFunction options[k]
        options[k] = {animator: options[k]}
      else
        throw new Error "act: animator is not string or function"
    new Implicit act, obj, options

  _defaultInterpolationForValue = (v) ->
    if _.isNumber v then linearAnimator else setAtEndAnimator

  # _rootScheduler
  # ---------------------------------------------------------------------
  # act root scheduler
  act._rootScheduler = new Scheduler()
  act.play()

  # End actGenerator
  return act

# act.Animator
# ---------------------------------------------------------------------
linearAnimator = (obj, destinations) ->
  initials = {}
  finals = {}
  for k, v of destinations
    # Handle operators
    if _.isObject v
      ops = _.keys v
      throw (new Error "act: expected only one operator (#{ops}) for key #{k}") unless ops.length == 1
      destinations[k] = op: ops[0], value: v[ops[0]]
    else
      destinations[k] = op: '=', value: v

    initials[k] = obj[k]
    finals[k] = _final initials[k], destinations[k]['op'], destinations[k]['value']
  (t) ->
    changed = {}
    for k, v of finals
      changed[k] = _interp initials[k], finals[k], t
    changed

_interp = (a, b, t) -> a * (1.0 - t) + b * t

_final = (initial, op, value) ->
  if op == '==' or op == '='
    return value
  else if op == '/='
    return initial / value
  else if op == '*='
    return initial * value
  else if op == '-='
    return initial - value
  else if op == '+='
    return initial + value

# setAtEndAnimator
# ---------------------------------------------------------------------
setAtEndAnimator = (obj, destinations) ->
  initials = _.pick obj, _.keys(destinations)...
  finals = _.clone destinations
  (t) -> _.clone (if t < 1 then initials else finals)

# Animator short names
# ---------------------------------------------------------------------
mapAnimatorFromName =
  'setAtEnd':       setAtEndAnimator
  'linear':         linearAnimator
  # 'type':           typeByItemAnimator          # Works on arrays and strings
  # 'typeByWord':     typeByWordAnimator          # Works on strings only
  # 'step':           stepAnimator
  # 'color':          colorAnimator
  # 'region':         regionAnimator
  # 'stringTypeWord': stringTypeByWordAnimator

# act.Task
# ---------------------------------------------------------------------

class Task
  constructor: (@obj, @destination, @act, options = {}) ->
    for k, v of @destination
      throw new Error('act: functions cannot be animated') if _.isFunction v
    if options.duration? and options.duration <= 0
      throw new Error 'act: duration must be positive'
    @duration = options.duration || 1
    @started = options.started || ->
    @completed = options.completed || ->
    @_easing = options.easing || EaseLinear

    @_animator = if _.isString options.animator
        mapAnimatorFromName[options.animator]
      else
        options.animator || linearAnimator

    @startTime = 0
    @_elapsed = 0

  update: (time) ->
    # Start if we need to
    elapsed = time - @startTime
    if (@_elapsed <= 0) and (elapsed <= 0)
      return     # Haven't started yet
    if (@_elapsed <= 0) and (elapsed > 0)
      @_start()  # Forwards over start point
    else if (@_elapsed >= @duration) and (elapsed >= @duration)
      return     # Already completed

    if (elapsed < 0)
      elapsed = 0    # Clamp elapsed at start time
    if (elapsed > @duration)
      elapsed = @duration     # Clamp elapsed at end time

    # Update the children
    eased = @_easing (elapsed / @duration)
    extender = (@_interpolator eased)
    for k, v of extender
      if store = act._implicitPropertyStore @obj, k
        store.value = v
      else
        @obj[k] = v

    if (elapsed >= @duration)
      @_complete()

    @_elapsed = elapsed

  _start: ->
    @_interpolator = @_animator @obj, @destination, @act

    finalValue = @_interpolator 1
    initialValue = @_interpolator 0
    @obj.actBefore? initialValue, finalValue
    @started?()

  _complete: ->
    initialValue = @_interpolator 0
    finalValue = @_interpolator 1
    @obj.actAfter? initialValue, finalValue
    @completed?()

_maxEndTime = (tasks) -> _.reduce tasks, ((acc,task) -> Math.max acc, (task.startTime + task.duration)), 0

# Easing
# ---------------------------------------------------------------------

Ease = {}
Ease.EaseLinear     =   EaseLinear      = (v) -> v
Ease.EaseIn         =   EaseIn          = (v) -> v * v * v
Ease.EaseOut        =   EaseOut         = (v) -> 1.0 - EaseIn(1.0 - v)
Ease.EaseIn2        =   EaseIn2         = (v) -> v * v
Ease.EaseOut2       =   EaseOut2        = (v) -> 1.0 - EaseIn(1.0 - v)
Ease.EaseInOut      =   EaseInOut       = (v) -> if v < 0.5 then EaseIn(v * 2) / 2 else EaseOut(v * 2 - 1.0) / 2 + 0.5
Ease.EaseInOut2     =   EaseInOut2      = (v) -> (3 * v * v) - 2 * v * v * v
Ease.EaseOutBounce  =   EaseOutBounce   = (v) ->
  if (v < 1 / 2.75)
    7.5625 * v * v
  else if (v < 2 / 2.75)
    v -= 1.5 / 2.75
    7.5625 * v * v + 0.75
  else if (v < 2.5 / 2.75)
    v -= 2.25 / 2.75
    7.5625 * v * v + 0.9375
  else
    v -= 2.625 / 2.75
    7.5625 * v * v + 0.984375

# Transaction
# ---------------------------------------------------------------------
class Transaction
  constructor: (fields) ->
    @_serial = fields.serial
    @_rate = fields.rate
    throw new Error 'act: rate must be positive' unless @_rate > 0
    @_easing = fields.easing
    @_tasks = fields.tasks
    @_reverseTasks = (@_tasks.slice 0).reverse()
    @_started = fields.started
    @_completed = fields.completed
    @_elapsed = 0
    @duration = (_maxEndTime @_tasks) / @_rate
    @startTime = 0

  update: (time) ->
    # Start if we need to
    elapsed = time - @startTime
    if (@_elapsed <= 0) and (elapsed <= 0)
      return     # Haven't started yet
    if (@_elapsed <= 0) and (elapsed > 0)
      @_start()  # Forwards over start point
    else if (@_elapsed >= @duration) and (elapsed >= @duration)
      return     # Already completed

    if (elapsed < 0)
      elapsed = 0             # Clamp elapsed at start time
    if (elapsed > @duration)
      elapsed = @duration     # Clamp elapsed at end time

    # Update the children
    tasks = if elapsed >= @_elapsed then @_tasks else @_reverseTasks
    eased = (@_easing elapsed / @duration) * @duration * @_rate
    for task in @_tasks
      task.update eased

    if (elapsed >= @duration)
      @_complete()

    @_elapsed = elapsed

  # Start calcuates duration and sets up everything
  _start: ->
    @_started?()
  _complete: ->
    @_completed?()

# TransactionBuilder
# ---------------------------------------------------------------------
_default = (a = b, b) ->
  a

class TransactionBuilder
  constructor: (fields = {}) ->
    @rate      = _default fields.rate, 1
    @started   = fields.started || ->
    @completed = fields.completed || ->
    @serial    = fields.serial || false
    @easing    = fields.easing || EaseLinear
    @_tasks    = []
  addTask: (task) ->
    @_tasks.push task
  transaction: ->
    if @serial
      t = 0
      for task in @_tasks
        task.startTime = t
        t += task.duration
    new Transaction
      rate: @rate
      started: @started
      completed: @completed
      serial: @serial
      easing: @easing
      tasks: @_tasks

# Scheduler
# ---------------------------------------------------------------------
# Transactions are added to the scheduler
class Scheduler
  constructor: (options = {}) ->
    @_tasks = []
    @rate = options.rate || 1
    @_elapsed = 0

  tick: (dt) ->
    @_elapsed += dt * @rate
    incompleteTasks = []
    changed = @_tasks.length
    for task in @_tasks
      task.update @_elapsed
      # Don't keep tasks that have ended
      if task.startTime + task.duration >= @_elapsed
        incompleteTasks.push task
    @_tasks = incompleteTasks
    changed

  # Add
  addTask: (task) ->
    task.startTime = @_elapsed
    @_tasks.push task

  @rate = 1.0


# Implicit
# ---------------------------------------------------------------------
# Proxy assignments to create implicit animations
class Implicit
  constructor: (@_act, @_obj, options) ->
    _defineAnimatedProperty = (k, actOptions) =>
      Object.defineProperty @, k,
        get: => @_obj[k]
        set: (v) => @_act @_obj, (_objectFromKeysAndValues k, v), actOptions
    _definePassthroughProperty = (k) =>
      Object.defineProperty @, k,
        get: => @_obj[k]
        set: (v) => @_obj[k] = v
    _proxyFunction = (k) =>
      @[k] = ->
        @_obj[k].apply @, arguments

    # Proxy the given setters, with animation
    for k, opts of options
      do (k, opts) =>
        if opts == null or opts.animator == null
          _definePassthroughProperty k
        else
          _defineAnimatedProperty k, opts

    # Proxy all methods and remaining non-animated properties
    for k, v of @_obj
      do (k, v) =>
        # Functions apply as if from the original object
        if _.isFunction v
          _proxyFunction k
        else if not @[k]?
          # Pass through properties that aren't animated to the original object
          _definePassthroughProperty k

_objectFromKeysAndValues = ->
  obj = Object.create null
  i =  0
  while i + 1 < arguments.length
    k = arguments[i]
    v = arguments[i + 1]
    obj[k] = v
    i += 2
  obj

# _setTimeout
# ---------------------------------------------------------------------
# Abstract setTimeout to

_requestAnimFrame = ->
  if window?
    window.requestAnimationFrame       ||
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame    ||
    window.oRequestAnimationFrame      ||
    window.msRequestAnimationFrame

_setTimeout = (timeSeconds, fn) ->
  if requestAnimFrame = _requestAnimFrame()
    requestAnimFrame fn
  else
    setTimeout fn, timeSeconds * 1000

# _getTime
# ---------------------------------------------------------------------
# Get current time

_getTime = ->
  (new Date).getTime() * 0.001


# _smoothTicks
# ---------------------------------------------------------------------
# Smooths ticks
_smoothTicks = (simulate, tick, fn) ->
  return fn
  ###
  # TODO: Think about this later
  return fn unless simulate
  throw (new Error 'tick must be positive') unless tick > 0
  timeElapsed = 0
  (dt) ->
    timeElapsed += dt
    while timeElapsed > tick
      fn tick
      timeElapsed -= tick
  ###

# Array Remove - By John Resig (MIT Licensed)
# ---------------------------------------------------------------------
Array.prototype.remove = (from, to) ->
  rest = this.slice((to or from) + 1 or this.length)
  @length = if from < 0 then this.length + from else from
  @push.apply this, rest

# act
# ---------------------------------------------------------------------
_extendEventFunctions = (obj) ->
  throw (new Error 'act: object already has event functions') if obj.on? or obj.off? or obj.trigger?
  _eventHandlers = {}

  obj.on = (eventName, cb, context) ->
    throw (new Error "act.on: eventName is not a string (#{eventName})") unless _.isString eventName
    throw (new Error "act.on: eventHandler is not a function (eventName: #{eventName}, eventHandler: #{cb})") unless _.isFunction cb
    eventNames = eventName.split ' '
    eventHandler = cb: cb, context: context
    for name in eventNames
      if name.length
        _eventHandlers[name] = [] unless _eventHandlers[name]
        _eventHandlers[name].push eventHandler

    -> obj.off eventName, cb, context

  obj.off = (eventName, cb, context) ->
    if eventName
      eventHandlersToChange = {}
      eventNames = eventName.split ' '
      for name in eventNames
        if name.length and _eventHandlers[name]
          eventHandlersToChange[name] = _eventHandlers[name]
    else
      eventHandlersToChange = _eventHandlers

    # Match cb and context
    for name, handlers of eventHandlersToChange
      _eventHandlers[name] = _.reject handlers, (handler) ->
        if cb
          return false unless handler.cb == cb
        if context
          return false unless handler.context == context
        true
    null

  obj.trigger = (eventName) ->
    eventNames = eventName.split ' '
    for name in eventNames
      if name.length and _eventHandlers[name]
        for handler in _eventHandlers[name]
          args = Array.prototype.slice.call arguments, 1
          handler.cb.apply (handler.context || obj), args
    null


# act
# ---------------------------------------------------------------------
act = actGenerator()


# Version
# ---------------------------------------------------------------------
act.version = '0.0.6'

# Export
# ---------------------------------------------------------------------
module.exports = act



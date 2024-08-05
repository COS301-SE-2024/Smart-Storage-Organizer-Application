_ = require 'underscore'


module.exports =

  #──────────────────────────────────────────────────────
  # itShouldBeAFunction
  #──────────────────────────────────────────────────────

  itShouldBeAFunction: (fn) ->
    it 'should be a function', ->
      fn.should.be.a 'function'

  subsetEqual: (small, big) ->
    throw 'subsetEqual: arguments are not objects' unless _.isObject(small) and _.isObject(big)
    for k,v of small
      return false unless _.isEqual small[k], big[k]
    true

  toStringDeep = (thing, indent = '') ->
    recurse = (thing2) ->
      toStringDeep thing2, (indent + '  ')

    if _.isNull thing
      return 'null'
    if _.isUndefined thing
      return 'undefined'
    if _.isString thing
      return "'#{thing}'"
    if _.isNumber thing
      return "#{thing}"
    if (_.isFunction thing)
      return thing.toString()

    if _.isArray thing
      str = "[\n"
      comma = ''
      for v in thing
        str += "#{comma}#{indent}#{recurse v}"
        comma = ',\n'
      str += "\n#{indent}]"
      return str
    if _.isObject thing
      str = "{\n"
      comma = ''
      for k, v of thing
        str += "#{comma}#{indent}#{recurse k}: #{recurse v}"
        comma = ',\n'
      str += "\n#{indent}}"
      return str
    # Function, regexp
    return thing.toString()


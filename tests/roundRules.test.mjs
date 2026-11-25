import test from 'node:test'
import assert from 'node:assert/strict'
import { getRoundRules } from '../src/roundRules.ts'

const root = { left: ['C', 'B'], right: ['S'] }

test('Ninja allows choosing a side before play and locks it once started', () => {
  assert.deepEqual(getRoundRules('ninja', root, 'right', false, 40, 0), {
    totalHooks: 1, roundComplete: false, canSwitchSide: true, canStart: true,
  })
  assert.equal(getRoundRules('ninja', root, 'right', true, 39, 0).canSwitchSide, false)
  assert.deepEqual(getRoundRules('ninja', root, 'right', false, 39, 1), {
    totalHooks: 1, roundComplete: true, canSwitchSide: false, canStart: true,
  })
  assert.equal(getRoundRules('ninja', root, 'right', false, 0, 0).canSwitchSide, false)
})

test('Ninja cannot start a side without hooks', () => {
  assert.deepEqual(getRoundRules('ninja', { left: [], right: ['S'] }, 'left', false, 40, 0), {
    totalHooks: 0, roundComplete: false, canSwitchSide: true, canStart: false,
  })
})

test('Practice counts both sides; Battle never allows switching', () => {
  assert.equal(getRoundRules('practice', root, 'left', true, 40, 1).totalHooks, 3)
  assert.equal(getRoundRules('practice', root, 'left', true, 40, 1).canSwitchSide, true)
  assert.equal(getRoundRules('battle', root, 'left', false, 40, 0).canSwitchSide, false)
})

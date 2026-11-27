import test from 'node:test'
import assert from 'node:assert/strict'
import { keyboardKeys, keyLabel, physicalKey } from '../src/keyboard.ts'

test('Spanish keyboard adds the four encoded letters with readable labels', () => {
  assert.deepEqual(keyboardKeys('ES').slice(-4).map(keyLabel), ['CH', 'LL', 'Ñ', 'RR'])
  assert.equal(keyboardKeys('EN').length, 26)
  assert.equal(keyboardKeys('ES').length, 28)
  assert.equal(keyboardKeys('ES').includes('K'), false)
  assert.equal(keyboardKeys('ES').includes('W'), false)
})

test('physical keyboard recognizes Spanish letter shortcuts only in Spanish', () => {
  for (const key of ['1', '2', '3', '4']) {
    assert.equal(physicalKey(key, 'ES'), key)
    assert.equal(physicalKey(key, 'EN'), undefined)
  }
  assert.equal(physicalKey('ñ', 'ES'), '3')
  assert.equal(physicalKey('Ñ', 'EN'), undefined)
  assert.equal(physicalKey('a', 'ES'), 'A')
  assert.equal(physicalKey('k', 'ES'), undefined)
  assert.equal(physicalKey('w', 'ES'), undefined)
  assert.equal(physicalKey('Enter', 'ES'), undefined)
})

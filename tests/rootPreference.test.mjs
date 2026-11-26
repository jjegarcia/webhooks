import test from 'node:test'
import assert from 'node:assert/strict'
import { loadRoot, saveRoot } from '../src/rootPreference.ts'

const roots = [{ value: 'AT' }, { value: 'CAT' }]

test('saves and restores the last selected root separately by language', () => {
  const values = new Map()
  const storage = () => ({
    getItem: (key) => values.get(key) ?? null,
    setItem: (key, value) => values.set(key, value),
  })
  assert.equal(loadRoot('EN', roots, storage), null)
  saveRoot('EN', 'CAT', storage)
  saveRoot('ES', 'AT', storage)
  assert.equal(loadRoot('EN', roots, storage), 'CAT')
  assert.equal(loadRoot('ES', roots, storage), 'AT')
})

test('ignores unknown roots and reports unavailable storage', () => {
  const originalWarn = console.warn
  const warnings = []
  console.warn = (...args) => warnings.push(args)
  try {
    assert.equal(loadRoot('EN', roots, () => ({ getItem: () => 'DOG' })), null)
    const unavailable = () => { throw new Error('Storage unavailable') }
    assert.equal(loadRoot('EN', roots, unavailable), null)
    saveRoot('EN', 'AT', unavailable)
    assert.equal(warnings.length, 3)
  } finally {
    console.warn = originalWarn
  }
})

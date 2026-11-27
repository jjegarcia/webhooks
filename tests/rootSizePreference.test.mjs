import test from 'node:test'
import assert from 'node:assert/strict'
import { loadBattleRootSize, loadRootSize, saveBattleRootSize, saveRootSize } from '../src/rootSizePreference.ts'

test('saves and restores the last selected root size', () => {
  const values = new Map()
  const storage = () => ({
    getItem: (key) => values.get(key) ?? null,
    setItem: (key, value) => values.set(key, value),
  })
  assert.equal(loadRootSize(storage), 2)
  saveRootSize(4, storage)
  assert.equal(loadRootSize(storage), 4)
  saveRootSize(3, storage)
  assert.equal(loadRootSize(storage), 3)
})

test('saves the battle root size separately for use after reload', () => {
  const values = new Map()
  const storage = () => ({
    getItem: (key) => values.get(key) ?? null,
    setItem: (key, value) => values.set(key, value),
  })
  saveRootSize(3, storage)
  saveBattleRootSize(4, storage)
  assert.equal(loadRootSize(storage), 3)
  assert.equal(loadBattleRootSize(storage), 4)
})

test('falls back to two-letter roots and reports unavailable or invalid storage', () => {
  const originalWarn = console.warn
  const warnings = []
  console.warn = (...args) => warnings.push(args)
  try {
    assert.equal(loadRootSize(() => ({ getItem: () => '5' })), 2)
    const unavailable = () => { throw new Error('Storage unavailable') }
    assert.equal(loadRootSize(unavailable), 2)
    saveRootSize(4, unavailable)
    assert.equal(warnings.length, 3)
  } finally {
    console.warn = originalWarn
  }
})

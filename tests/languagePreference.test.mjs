import test from 'node:test'
import assert from 'node:assert/strict'
import { loadLanguage, saveLanguage } from '../src/languagePreference.ts'

test('saves and restores the last selected language', () => {
  const values = new Map()
  const storage = () => ({
    getItem: (key) => values.get(key) ?? null,
    setItem: (key, value) => values.set(key, value),
  })
  assert.equal(loadLanguage(storage), 'EN')
  saveLanguage('ES', storage)
  assert.equal(loadLanguage(storage), 'ES')
  saveLanguage('EN', storage)
  assert.equal(loadLanguage(storage), 'EN')
})

test('falls back to English and reports unavailable or invalid storage', () => {
  const originalWarn = console.warn
  const warnings = []
  console.warn = (...args) => warnings.push(args)
  try {
    assert.equal(loadLanguage(() => ({ getItem: () => 'FR' })), 'EN')
    const unavailable = () => { throw new Error('Storage unavailable') }
    assert.equal(loadLanguage(unavailable), 'EN')
    saveLanguage('ES', unavailable)
    assert.equal(warnings.length, 3)
  } finally {
    console.warn = originalWarn
  }
})

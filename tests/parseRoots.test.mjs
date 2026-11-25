import test from 'node:test'
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import { parseRoots } from '../src/parseRoots.ts'
import { displayWord } from '../src/keyboard.ts'
import { randomBattleRoot } from '../src/battleRoot.ts'

const table = (name) => readFileSync(new URL(`../app/src/main/java/com/application/hooksPlus/tables/${name}.kt`, import.meta.url), 'utf8')
const spanish = parseRoots(['Hooks2_es', 'Hooks3_es', 'Hooks4_es', 'Hooks4b_es', 'Hooks4c_es'].map(table), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234')
const english = parseRoots(['Hooks2_en', 'Hooks3_en', 'Hooks3b_en', 'Hooks4_en', 'Hooks4b_en', 'Hooks4c_en'].map(table), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')

test('loads Spanish encoded hooks and roots instead of English data', () => {
  const find = (value) => spanish.roots.find((root) => root.value === value)
  assert.ok(find('AL').left.includes('1'))
  assert.ok(find('AR').left.includes('2'))
  assert.ok(find('AS').left.includes('3'))
  assert.ok(find('A4A').left.includes('1'))
  assert.equal(displayWord(`1${find('A4A').value}`), 'CHARRA')
  assert.ok(!english.roots.find((root) => root.value === 'AL').left.includes('1'))
  assert.deepEqual([2, 3, 4].map((size) => spanish.roots.some((root) => root.value.length === size)), [true, true, true])
})

test('rejects invalid records without creating unplayable roots', () => {
  const result = parseRoots(['WordStructure("1AL", "AL"), WordStructure("WRONG", "AL"), WordStructure("a3a", "3a")'], 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234')
  assert.equal(result.invalid, 1)
  assert.deepEqual(result.roots, [
    { value: 'AL', left: ['1'], right: [] },
    { value: '3A', left: ['A'], right: [] },
  ])
})

test('preserves root-only entries used by the English table', () => {
  assert.deepEqual(parseRoots(['WordStructure("TEST", "TEST")'], 'ABCDEFGHIJKLMNOPQRSTUVWXYZ').roots, [
    { value: 'TEST', left: [], right: [] },
  ])
})

test('battle selects a playable Spanish root on its selected side', () => {
  for (const value of [0, 0.3, 0.6, 0.99]) {
    const selection = randomBattleRoot(spanish.roots, () => value)
    const root = spanish.roots[selection.index]
    assert.equal(root.value.length, selection.size)
    assert.ok(root[selection.side].length > 0)
  }
})

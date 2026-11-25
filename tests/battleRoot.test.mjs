import test from 'node:test'
import assert from 'node:assert/strict'
import { randomBattleRoot } from '../src/battleRoot.ts'

const roots = [
  { value: 'AT', left: ['C'], right: [] },
  { value: 'IN', left: [], right: ['K'] },
  { value: 'CAT', left: [], right: ['S'] },
  { value: 'CARE', left: ['S'], right: [] },
  { value: 'BARE', left: [], right: [] },
]

test('chooses a side, root size, and playable root', () => {
  const choices = [0.99, 0, 0.99]
  assert.deepEqual(randomBattleRoot(roots, () => choices.shift()), { index: 1, size: 2, side: 'right' })
  assert.deepEqual(randomBattleRoot(roots, () => 0), { index: 0, size: 2, side: 'left' })
  assert.deepEqual(randomBattleRoot(roots, () => 0.99), { index: 2, size: 3, side: 'right' })
})

test('skips unavailable sizes, sides, and roots without hooks on the selected side', () => {
  assert.deepEqual(randomBattleRoot(roots.slice(3), () => 0.99), { index: 0, size: 4, side: 'left' })
  assert.throws(() => randomBattleRoot([roots[4]]), /No playable battle roots/)
})

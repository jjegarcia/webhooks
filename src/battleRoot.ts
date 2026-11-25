type BattleRoot = { value: string; left: string[]; right: string[] }
export type RootSize = 2 | 3 | 4
export type Side = 'left' | 'right'
const rootSizes: readonly number[] = [2, 3, 4]

export function randomBattleRoot(roots: BattleRoot[], random = Math.random): { index: number; size: RootSize; side: Side } {
  const sides: Side[] = ['left', 'right']
  const availableSides = sides.filter((side) => roots.some((root) =>
    rootSizes.includes(root.value.length) && root[side].length > 0))
  if (availableSides.length === 0) throw new Error('No playable battle roots are available')

  const side = availableSides[Math.floor(random() * availableSides.length)]
  const groups = ([2, 3, 4] as const).map((size) => ({
    size,
    entries: roots
        .map((root, index) => ({ root, index }))
        .filter(({ root }) => root.value.length === size && root[side].length > 0),
  })).filter((group) => group.entries.length > 0)

  const group = groups[Math.floor(random() * groups.length)]
  return {
    index: group.entries[Math.floor(random() * group.entries.length)].index,
    size: group.size,
    side,
  }
}

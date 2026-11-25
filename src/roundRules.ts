import type { Side } from './battleRoot'

export type Mode = 'practice' | 'ninja' | 'battle'
type RootHooks = Record<Side, readonly string[]>

export function getRoundRules(mode: Mode, root: RootHooks, side: Side, playing: boolean, seconds: number, completedCount: number) {
  const totalHooks = mode === 'practice' ? root.left.length + root.right.length : root[side].length
  const roundComplete = totalHooks > 0 && completedCount === totalHooks
  return {
    totalHooks,
    roundComplete,
    canSwitchSide: mode === 'practice' ? !roundComplete : mode === 'ninja' && !playing && seconds > 0 && !roundComplete,
    canStart: mode !== 'ninja' || roundComplete || seconds === 0 || totalHooks > 0,
  }
}

export type Root = { value: string; left: string[]; right: string[] }

export function parseRoots(tables: string[], allowedSymbols: string): { roots: Root[]; invalid: number } {
  const rootMap = new Map<string, Root>()
  let invalid = 0

  for (const table of tables) {
    for (const [, rawWord, rawRoot] of table.matchAll(/WordStructure\(\s*"([^"]+)"\s*,\s*"([^"]+)"\s*\)/g)) {
      const word = rawWord.toUpperCase()
      const value = rawRoot.toUpperCase()
      if (![...word, ...value].every((letter) => allowedSymbols.includes(letter)) ||
          (word !== value && (word.length !== value.length + 1 ||
          (!word.startsWith(value) && !word.endsWith(value))))) {
        invalid++
        continue
      }

      let root = rootMap.get(value)
      if (!root) {
        root = { value, left: [], right: [] }
        rootMap.set(value, root)
      }
      if (word === value) continue
      if (word.endsWith(value) && !root.left.includes(word[0])) root.left.push(word[0])
      const last = word.at(-1)!
      if (word.startsWith(value) && !root.right.includes(last)) root.right.push(last)
    }
  }

  if (rootMap.size === 0) throw new Error('The hook tables contain no valid roots')
  return { roots: [...rootMap.values()], invalid }
}

import hooks2 from '../app/src/main/java/com/application/hooksPlus/tables/Hooks2_en.kt?raw'
import hooks3 from '../app/src/main/java/com/application/hooksPlus/tables/Hooks3_en.kt?raw'
import hooks3b from '../app/src/main/java/com/application/hooksPlus/tables/Hooks3b_en.kt?raw'
import hooks4 from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4_en.kt?raw'
import hooks4b from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4b_en.kt?raw'
import hooks4c from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4c_en.kt?raw'

type Root = { value: string; left: string[]; right: string[]; hint: string }

const rootMap = new Map<string, Root>()

for (const hookTable of [hooks2, hooks3, hooks3b, hooks4, hooks4b, hooks4c]) {
  for (const [, word, value] of hookTable.matchAll(/WordStructure\("([A-Z]+)",\s*"([A-Z]+)"\)/g)) {
    let root = rootMap.get(value)
    if (!root) {
      root = { value, left: [], right: [], hint: 'Add a letter before or after this root' }
      rootMap.set(value, root)
    }

    if (word === value) continue
    if (word.length !== value.length + 1) {
      throw new Error(`Invalid hook ${word} for root ${value}`)
    }
    if (word.endsWith(value) && !root.left.includes(word[0])) {
      root.left.push(word[0])
    }
    if (word.startsWith(value) && !root.right.includes(word.at(-1)!)) {
      root.right.push(word.at(-1)!)
    }
  }
}

if (rootMap.size === 0) {
  throw new Error('The hook table contains no roots')
}

const startingRoots = ['AT', 'IN', 'AR', 'AN', 'AM']
export const roots = [
  ...startingRoots.map((value) => {
    const root = rootMap.get(value)
    if (!root) throw new Error(`Missing starting root ${value}`)
    return root
  }),
  ...Array.from(rootMap.values()).filter((root) => !startingRoots.includes(root.value)),
]

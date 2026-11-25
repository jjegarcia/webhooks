import hooks2En from '../app/src/main/java/com/application/hooksPlus/tables/Hooks2_en.kt?raw'
import hooks3En from '../app/src/main/java/com/application/hooksPlus/tables/Hooks3_en.kt?raw'
import hooks3bEn from '../app/src/main/java/com/application/hooksPlus/tables/Hooks3b_en.kt?raw'
import hooks4En from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4_en.kt?raw'
import hooks4bEn from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4b_en.kt?raw'
import hooks4cEn from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4c_en.kt?raw'
import hooks2Es from '../app/src/main/java/com/application/hooksPlus/tables/Hooks2_es.kt?raw'
import hooks3Es from '../app/src/main/java/com/application/hooksPlus/tables/Hooks3_es.kt?raw'
import hooks4Es from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4_es.kt?raw'
import hooks4bEs from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4b_es.kt?raw'
import hooks4cEs from '../app/src/main/java/com/application/hooksPlus/tables/Hooks4c_es.kt?raw'
import { parseRoots } from './parseRoots'

const english = parseRoots([hooks2En, hooks3En, hooks3bEn, hooks4En, hooks4bEn, hooks4cEn], 'ABCDEFGHIJKLMNOPQRSTUVWXYZ')
const spanish = parseRoots([hooks2Es, hooks3Es, hooks4Es, hooks4bEs, hooks4cEs], 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234')
if (english.invalid) console.warn(`Skipped ${english.invalid} invalid English hook entries`)
if (spanish.invalid) console.warn(`Skipped ${spanish.invalid} invalid Spanish hook entries`)

const startingRoots = ['AT', 'IN', 'AR', 'AN', 'AM']
export const rootsByLanguage = {
  EN: [
    ...startingRoots.map((value) => {
      const root = english.roots.find((item) => item.value === value)
      if (!root) throw new Error(`Missing starting root ${value}`)
      return root
    }),
    ...english.roots.filter((root) => !startingRoots.includes(root.value)),
  ],
  ES: spanish.roots,
}

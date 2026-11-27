import type { RootSize } from './battleRoot'

const storageKey = 'hooks.rootSize'
const battleStorageKey = 'hooks.battleRootSize'

function loadSize(key: string, getStorage: () => Storage): RootSize {
  try {
    const saved = getStorage().getItem(key)
    if (saved === '2') return 2
    if (saved === '3') return 3
    if (saved === '4') return 4
    if (saved !== null) console.warn(`Ignoring unsupported saved root size for ${key}: ${saved}`)
  } catch (error) {
    console.warn(`Unable to load the saved root size for ${key}`, error)
  }
  return 2
}

function saveSize(key: string, size: RootSize, getStorage: () => Storage): void {
  try {
    getStorage().setItem(key, String(size))
  } catch (error) {
    console.warn(`Unable to save the selected root size for ${key}`, error)
  }
}

export function loadRootSize(getStorage: () => Storage = () => window.localStorage): RootSize {
  return loadSize(storageKey, getStorage)
}

export function saveRootSize(size: RootSize, getStorage: () => Storage = () => window.localStorage): void {
  saveSize(storageKey, size, getStorage)
}

export function loadBattleRootSize(getStorage: () => Storage = () => window.localStorage): RootSize {
  return loadSize(battleStorageKey, getStorage)
}

export function saveBattleRootSize(size: RootSize, getStorage: () => Storage = () => window.localStorage): void {
  saveSize(battleStorageKey, size, getStorage)
}

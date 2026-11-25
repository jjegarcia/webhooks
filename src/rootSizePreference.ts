import type { RootSize } from './battleRoot'

const storageKey = 'hooks.rootSize'

export function loadRootSize(getStorage: () => Storage = () => window.localStorage): RootSize {
  try {
    const saved = getStorage().getItem(storageKey)
    if (saved === '2') return 2
    if (saved === '3') return 3
    if (saved === '4') return 4
    if (saved !== null) console.warn(`Ignoring unsupported saved root size: ${saved}`)
  } catch (error) {
    console.warn('Unable to load the saved root size', error)
  }
  return 2
}

export function saveRootSize(size: RootSize, getStorage: () => Storage = () => window.localStorage): void {
  try {
    getStorage().setItem(storageKey, String(size))
  } catch (error) {
    console.warn('Unable to save the selected root size', error)
  }
}

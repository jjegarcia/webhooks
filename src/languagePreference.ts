import type { Language } from './keyboard'

const storageKey = 'hooks.language'

export function loadLanguage(getStorage: () => Storage = () => window.localStorage): Language {
  try {
    const saved = getStorage().getItem(storageKey)
    if (saved === 'EN' || saved === 'ES') return saved
    if (saved !== null) console.warn(`Ignoring unsupported saved language: ${saved}`)
  } catch (error) {
    console.warn('Unable to load the saved language', error)
  }
  return 'EN'
}

export function saveLanguage(language: Language, getStorage: () => Storage = () => window.localStorage): void {
  try {
    getStorage().setItem(storageKey, language)
  } catch (error) {
    console.warn('Unable to save the selected language', error)
  }
}

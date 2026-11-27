import { Fragment, useEffect, useMemo, useRef, useState } from 'react'
import hooksLogo from '../app/src/main/res/drawable/hooks_logo.png'
import { randomBattleRoot } from './battleRoot'
import type { RootSize, Side } from './battleRoot'
import { displayWord, keyLabel, keyboardKeys, physicalKey } from './keyboard'
import type { Language } from './keyboard'
import { loadLanguage, saveLanguage } from './languagePreference'
import { getRoundRules } from './roundRules'
import type { Mode } from './roundRules'
import { loadRoot, saveRoot } from './rootPreference'
import { loadBattleRootSize, loadRootSize, saveBattleRootSize, saveRootSize } from './rootSizePreference'
import { rootsByLanguage } from './roots'


const copy = {
  EN: {
    tagline: 'Wordplay, one letter at a time.',
    howToPlay: 'How to play',
    practice: 'Practice',
    ninja: 'Ninja',
    battle: 'Battle',
    practiceTitle: 'A little wordplay goes a long way.',
    practiceSubtitle: 'Build words around the root. How many hooks can you find?',
    root: 'YOUR ROOT',
    score: 'SCORE',
    streak: 'STREAK',
    words: 'WORDS',
    left: 'Build to the left',
    right: 'Build to the right',
    leftHint: 'Add a letter before the root',
    rightHint: 'Add a letter after the root',
    keyboard: 'Choose a letter',
    keyboardHint: 'Or use your keyboard',
    hookHints: 'Hook hints',
    hintsOn: 'On',
    hintsOff: 'Off',
    hiddenHook: 'Undiscovered hook',
    correctGuess: 'Correct guess',
    wrongGuess: 'Incorrect guess',
    submittedGuess: 'Submitted guess',
    resultsPending: 'Results will be revealed at the end of the round.',
    correctAnswers: 'Correct answers',
    foundAnswer: 'Found',
    missedAnswer: 'Missed',
    available: 'Available hooks',
    leftHooks: 'BEFORE',
    rightHooks: 'AFTER',
    roots: 'Choose a root',
    rootLength: 'Root length',
    rootIndex: 'Jump to roots by first letter',
    newRound: 'New round',
    switchSide: 'Switch side',
    noHooksOnSide: 'No hooks on this side. Switch sides or choose another root.',
    nextRoot: 'Next root',
    stop: 'Stop round',
    stopped: 'Round finished.',
    start: 'Start round',
    rootHint: 'A fresh root is waiting',
    trail: 'Your word trail',
    trailHint: 'Each new word starts with the same root.',
    welcome: '',
    correct: 'Nice hook! Keep going.',
    incorrect: 'Not quite — try another letter.',
    repeated: 'You already found that hook.',
    completed: 'All hooks found. Brilliant work!',
    timeUp: "Time's up. What a run!",
    howTitle: 'The art of the hook',
    howBody: 'Start with the highlighted root. Add one letter before it or after it to make a word. Find every hook on one side, then keep going on the other.',
    timed: '40 second round',
    hint: 'Hint',
    modeNote: 'Choose a mode to make it your own.',
    solved: 'FOUND',
    remaining: 'left to find',
  },
  ES: {
    tagline: 'Juega con palabras, letra a letra.',
    howToPlay: 'Cómo jugar',
    practice: 'Práctica',
    ninja: 'Ninja',
    battle: 'Batalla',
    practiceTitle: 'Una letra puede llevarte muy lejos.',
    practiceSubtitle: 'Crea palabras alrededor de la raíz. ¿Cuántas puedes encontrar?',
    root: 'TU RAÍZ',
    score: 'PUNTOS',
    streak: 'RACHA',
    words: 'PALABRAS',
    left: 'Añade a la izquierda',
    right: 'Añade a la derecha',
    leftHint: 'Añade una letra antes de la raíz',
    rightHint: 'Añade una letra después de la raíz',
    keyboard: 'Elige una letra',
    keyboardHint: 'O usa tu teclado',
    hookHints: 'Pistas de ganchos',
    hintsOn: 'Activadas',
    hintsOff: 'Desactivadas',
    hiddenHook: 'Gancho sin descubrir',
    correctGuess: 'Acierto',
    wrongGuess: 'Error',
    submittedGuess: 'Intento enviado',
    resultsPending: 'Los resultados se mostrarán al finalizar la ronda.',
    correctAnswers: 'Respuestas correctas',
    foundAnswer: 'Encontrada',
    missedAnswer: 'Sin encontrar',
    available: 'Ganchos disponibles',
    leftHooks: 'ANTES',
    rightHooks: 'DESPUÉS',
    roots: 'Elige una raíz',
    rootLength: 'Longitud de la raíz',
    rootIndex: 'Ir a raíces por letra inicial',
    newRound: 'Nueva ronda',
    switchSide: 'Cambiar lado',
    noHooksOnSide: 'No hay ganchos en este lado. Cambia de lado o elige otra raíz.',
    nextRoot: 'Siguiente raíz',
    stop: 'Terminar ronda',
    stopped: 'Ronda terminada.',
    start: 'Empezar ronda',
    rootHint: 'Una nueva raíz te espera',
    trail: 'Tu camino de palabras',
    trailHint: 'Cada palabra nueva empieza con la misma raíz.',
    welcome: '',
    correct: '¡Buen gancho! Sigue así.',
    incorrect: 'No exactamente. Prueba otra letra.',
    repeated: 'Ya encontraste ese gancho.',
    completed: '¡Encontraste todos los ganchos!',
    timeUp: 'Se acabó el tiempo. ¡Qué buena ronda!',
    howTitle: 'El arte del gancho',
    howBody: 'Empieza con la raíz resaltada. Añade una letra antes o después para crear una palabra. Encuentra todos los ganchos de un lado y continúa por el otro.',
    timed: 'Ronda de 40 segundos',
    hint: 'Pista',
    modeNote: 'Elige un modo y hazlo tuyo.',
    solved: 'ENCONTRADAS',
    remaining: 'por encontrar',
  },
}

const timedModes: Mode[] = ['battle']
const modeIcons: Record<Mode, string> = {
  practice: '✳',
  ninja: '◈',
  battle: '⚔',
}

function getSavedRootSelection(language: Language): { rootIndex: number; rootSize: RootSize } {
  const roots = rootsByLanguage[language]
  const savedRoot = loadRoot(language, roots)
  const savedIndex = savedRoot === null ? -1 : roots.findIndex((root) => root.value === savedRoot)
  const savedLength = savedIndex === -1 ? null : roots[savedIndex].value.length
  const savedSize = savedLength === 2 || savedLength === 3 || savedLength === 4 ? savedLength : null
  const rootSize = savedSize ?? loadRootSize()
  return {
    rootSize,
    rootIndex: savedIndex === -1 || savedSize === null
      ? roots.findIndex((root) => root.value.length === rootSize)
      : savedIndex,
  }
}

function Icon({ name }: { name: 'spark' | 'clock' | 'arrow' | 'help' | 'leaf' | 'check' | 'refresh' }) {
  const common = { width: 18, height: 18, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true as const }

  if (name === 'clock') return <svg {...common}><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></svg>
  if (name === 'arrow') return <svg {...common}><path d="M5 12h14M13 6l6 6-6 6" /></svg>
  if (name === 'help') return <svg {...common}><circle cx="12" cy="12" r="9" /><path d="M9.6 9a2.5 2.5 0 0 1 4.8 1c0 1.8-2.4 2-2.4 4M12 17.5v.1" /></svg>
  if (name === 'check') return <svg {...common}><path d="m5 12 4 4L19 6" /></svg>
  if (name === 'refresh') return <svg {...common}><path d="M20 7v5h-5M4 17v-5h5" /><path d="M5.5 9A7 7 0 0 1 17 6.5L20 12M4 12l3 5.5A7 7 0 0 0 18.5 15" /></svg>
  if (name === 'leaf') return <svg {...common}><path d="M20 4c-8 0-14 3.8-14 10a6 6 0 0 0 6 6c6.2 0 8-7 8-16Z" /><path d="M4 21c3-5 7-8 12-11" /></svg>
  return <svg {...common}><path d="m12 3 1.7 5.3L19 10l-5.3 1.7L12 17l-1.7-5.3L5 10l5.3-1.7L12 3ZM19 15l.9 2.1L22 18l-2.1.9L19 21l-.9-2.1L16 18l2.1-.9L19 15Z" /></svg>
}

function App() {
  const [language, setLanguage] = useState<Language>(loadLanguage)
  const [mode, setMode] = useState<Mode>('practice')
  const [initialRootSelection] = useState(() => getSavedRootSelection(language))
  const [rootSize, setRootSize] = useState<RootSize>(initialRootSelection.rootSize)
  const [battleRootSize, setBattleRootSize] = useState<RootSize>(loadBattleRootSize)
  const [rootIndex, setRootIndex] = useState(initialRootSelection.rootIndex)
  const [attemptedRoots, setAttemptedRoots] = useState<string[]>([])
  const [completedRoots, setCompletedRoots] = useState<string[]>([])
  const [side, setSide] = useState<Side>('left')
  const [hintsEnabled, setHintsEnabled] = useState(false)
  const [usedHooks, setUsedHooks] = useState<string[]>([])
  const [wrongGuesses, setWrongGuesses] = useState<string[]>([])
  const [trail, setTrail] = useState<string[]>([])
  const [score, setScore] = useState(0)
  const [streak, setStreak] = useState(0)
  const [bestStreak, setBestStreak] = useState(0)
  const [misses, setMisses] = useState(0)
  const [message, setMessage] = useState('')
  const [playing, setPlaying] = useState(true)
  const [seconds, setSeconds] = useState(40)
  const [modal, setModal] = useState<'help' | null>(null)
  const rootPickerRef = useRef<HTMLDivElement>(null)

  const t = copy[language]
  const roots = rootsByLanguage[language]
  const keys = keyboardKeys(language)
  const sizedRoots = useMemo(() => roots
      .map((root, index) => ({ root, index }))
      .filter(({ root }) => root.value.length === rootSize), [roots, rootSize])
  const rootGroups = useMemo(() => keys.map((letter) => ({
    letter,
    entries: sizedRoots
        .filter(({ root }) => root.value.startsWith(letter))
        .sort((a, b) => displayWord(a.root.value).localeCompare(displayWord(b.root.value), language === 'ES' ? 'es' : 'en')),
  })).filter(({ entries }) => entries.length > 0), [sizedRoots, language])
  const currentRoot = roots[rootIndex]
  const completedCount = usedHooks.length
  const { totalHooks, roundComplete, canSwitchSide, canStart } = getRoundRules(mode, currentRoot, side, playing, seconds, completedCount)
  const isTimed = timedModes.includes(mode)
  const resultsHidden = isTimed && seconds > 0 && (mode === 'battle' || !roundComplete)
  const revealAnswers = isTimed && !resultsHidden
  const feedback = resultsHidden ? (usedHooks.length + wrongGuesses.length > 0 ? t.resultsPending : t.welcome) : message || t.welcome
  const hintsAvailable = mode !== 'ninja' && mode !== 'battle'
  const wordCount = trail.length + 1
  const progress = totalHooks > 0 ? (completedCount / totalHooks) * 100 : 0

  const keyboardMarkers = useMemo(() => {
    const markers = new Map<string, Side>()
    const activeLetters = side === 'left' ? currentRoot.left : currentRoot.right
    activeLetters.forEach((letter) => {
      if (!usedHooks.includes(`${side}:${letter}`)) markers.set(letter, side)
    })
    return markers
  }, [currentRoot, side, usedHooks])

  useEffect(() => {
    if (!isTimed || !playing || seconds <= 0) return
    const interval = window.setInterval(() => {
      setSeconds((value) => {
        if (value <= 1) {
          setPlaying(false)
          setMessage(t.timeUp)
          return 0
        }
        return value - 1
      })
    }, 1000)
    return () => window.clearInterval(interval)
  }, [isTimed, playing, seconds, t.timeUp])

  const selectMode = (nextMode: Mode) => {
    setMode(nextMode)
    if (nextMode === 'battle') {
      const nextRoot = randomBattleRoot(roots, Math.random, battleRootSize)
      setRootIndex(nextRoot.index)
      setSide(nextRoot.side)
    } else {
      const savedSelection = getSavedRootSelection(language)
      setRootSize(savedSelection.rootSize)
      setRootIndex(savedSelection.rootIndex)
      setSide('left')
    }
    setUsedHooks([])
    setWrongGuesses([])
    setTrail([])
    setStreak(0)
    setSeconds(40)
    setPlaying(!timedModes.includes(nextMode))
    setMessage('')
  }

  const resetRound = (nextRootIndex = rootIndex, nextSide: Side = 'left') => {
    setRootIndex(nextRootIndex)
    setUsedHooks([])
    setWrongGuesses([])
    setTrail([])
    setSide(nextSide)
    setSeconds(40)
    setPlaying(!isTimed)
    setMessage(t.welcome)
    setStreak(0)
  }

  const newRound = () => {
    if (mode === 'battle') {
      const nextRoot = randomBattleRoot(roots, Math.random, battleRootSize)
      resetRound(nextRoot.index, nextRoot.side)
    } else {
      resetRound()
    }
  }

  const nextBattleRoot = () => {
    const nextRoot = randomBattleRoot(roots, Math.random, battleRootSize)
    setRootIndex(nextRoot.index)
    setSide(nextRoot.side)
    setUsedHooks([])
    setWrongGuesses([])
    setTrail([])
    setMessage(t.welcome)
    setStreak(0)
  }

  const changeLanguage = () => {
    const nextLanguage = language === 'EN' ? 'ES' : 'EN'
    const nextRoots = rootsByLanguage[nextLanguage]
    saveLanguage(nextLanguage)
    setLanguage(nextLanguage)
    setAttemptedRoots([])
    setCompletedRoots([])
    setScore(0)
    setBestStreak(0)
    setMisses(0)
    if (mode === 'battle') {
      const nextRoot = randomBattleRoot(nextRoots, Math.random, battleRootSize)
      resetRound(nextRoot.index, nextRoot.side)
    } else {
      const nextRootIndex = nextRoots.findIndex((root) => root.value.length === rootSize)
      if (nextRootIndex === -1) throw new Error(`No ${rootSize}-letter roots for ${nextLanguage}`)
      saveRoot(nextLanguage, nextRoots[nextRootIndex].value)
      resetRound(nextRootIndex)
    }
    setMessage('')
  }

  const selectRootSize = (size: RootSize) => {
    const firstRootIndex = roots.findIndex((root) => root.value.length === size)
    if (firstRootIndex === -1) return
    setRootSize(size)
    saveRootSize(size)
    saveRoot(language, roots[firstRootIndex].value)
    resetRound(firstRootIndex)
  }

  const selectBattleRootSize = (size: RootSize) => {
    const nextRoot = randomBattleRoot(roots, Math.random, size)
    setBattleRootSize(size)
    saveBattleRootSize(size)
    resetRound(nextRoot.index, nextRoot.side)
  }

  const selectRoot = (index: number) => {
    const root = roots[index]
    if (!root) return
    const size = root.value.length
    if (size !== 2 && size !== 3 && size !== 4) throw new Error(`Unsupported root size: ${size}`)
    saveRoot(language, root.value)
    saveRootSize(size)
    setRootSize(size)
    resetRound(index)
  }

  const scrollToRootLetter = (letter: string) => {
    const picker = rootPickerRef.current
    const heading = picker?.querySelector<HTMLElement>(`[data-root-letter="${letter}"]`)
    if (picker && heading) {
      heading.focus({ preventScroll: true })
      picker.scrollTo({ top: heading.offsetTop })
    }
  }

  const submitLetter = (letter: string) => {
    if (!keys.includes(letter) || !playing || roundComplete) return
    const possibleLetters = side === 'left' ? currentRoot.left : currentRoot.right
    const key = `${side}:${letter}`
    if (usedHooks.includes(key)) {
      setMessage(t.repeated)
      return
    }
    setAttemptedRoots((value) => value.includes(currentRoot.value) ? value : [...value, currentRoot.value])
    if (!possibleLetters.includes(letter)) {
      setWrongGuesses((value) => value.includes(key) ? value : [...value, key])
      setMessage(t.incorrect)
      setStreak(0)
      setMisses((value) => value + 1)
      if (mode !== 'practice') setScore((value) => Math.max(0, value - 5))
      return
    }

    const nextUsed = [...usedHooks, key]
    const word = side === 'left' ? `${letter}${currentRoot.value}` : `${currentRoot.value}${letter}`
    setUsedHooks(nextUsed)
    setTrail((value) => [...value, word])
    setScore((value) => value + 10)
    setStreak((value) => {
      const next = value + 1
      setBestStreak((best) => Math.max(best, next))
      return next
    })

    const sideDone = possibleLetters.every((item) => nextUsed.includes(`${side}:${item}`))
    const allDone = nextUsed.length === totalHooks
    if (allDone) {
      if (nextUsed.length === currentRoot.left.length + currentRoot.right.length) {
        setCompletedRoots((value) => value.includes(currentRoot.value) ? value : [...value, currentRoot.value])
      }
      setMessage(t.completed)
      if (mode !== 'battle') setPlaying(false)
    } else if (!isTimed && sideDone && side === 'left') {
      setSide('right')
      setMessage(language === 'EN' ? 'Left side complete — now hook the other side.' : 'Lado izquierdo completo. ¡Vamos al otro!')
    } else {
      setMessage(t.correct)
    }
  }

  useEffect(() => {
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.altKey || event.ctrlKey || event.metaKey || modal) return
      if (event.key === 'Escape') {
        setModal(null)
        return
      }
      const letter = physicalKey(event.key, language)
      if (letter) submitLetter(letter)
      if (event.key === 'Backspace' && trail.length > 0 && !isTimed) {
        event.preventDefault()
        const lastHook = usedHooks.at(-1)!
        const lastSide = lastHook.startsWith('right:') ? 'right' : 'left'
        setUsedHooks((value) => value.slice(0, -1))
        setTrail((value) => value.slice(0, -1))
        setSide(lastSide)
        setScore((value) => Math.max(0, value - 10))
        setMessage('')
      }
    }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [modal, submitLetter, trail, usedHooks, side])

  const handleStart = () => {
    if (mode === 'ninja' && (seconds === 0 || roundComplete)) {
      newRound()
      return
    }
    if (seconds === 0 || roundComplete) newRound()
    setPlaying(true)
    setMessage('')
  }

  const handleStop = () => {
    setPlaying(false)
    setSeconds(0)
    setMessage(t.stopped)
  }

  const rootHint = language === 'EN' ? 'Add a letter before or after this root' : 'Añade una letra antes o después de esta raíz'
  const rootSideLabel = (targetSide: Side) => targetSide === 'left'
    ? `_${displayWord(currentRoot.value)}`
    : `${displayWord(currentRoot.value)}_`

  return (
      <div className="app-shell">
        <header className="topbar">
          <a className="brand" href="#" aria-label="Hooks home">
            <span className="brand-mark"><img src={hooksLogo} alt="" /></span>
            <span className="brand-name">hooks<span>.</span><small>{t.tagline}</small></span>
          </a>
          <div className="topbar-actions">
            <button className="icon-button help-button" aria-label={t.howToPlay} onClick={() => setModal('help')}><Icon name="help" /></button>
            <span className="topbar-divider" />
            <button className="language-toggle" onClick={changeLanguage} aria-label="Change language">
              <span className="language-globe">◎</span>{language}<span className="chevron">⌄</span>
            </button>
            <span className="avatar" aria-label="Guest player">G</span>
          </div>
        </header>

        <main className="page-content">
          <div className="intro-row">
            <div className="intro-copy">
              <div className="eyebrow"><span className="eyebrow-dot" /> THE WORD GAME WITH A TWIST</div>
              <h1>{t.practiceTitle}</h1>
              <p>{t.practiceSubtitle}</p>
            </div>
          </div>

          <div className="mode-row">
            <div className="mode-switch" role="group" aria-label="Game mode">
              {(['practice', 'ninja', 'battle'] as Mode[]).map((item) => (
                  <button key={item} className={`mode-option ${mode === item ? 'selected' : ''}`} onClick={() => selectMode(item)} aria-pressed={mode === item}>
                    <span className={`mode-symbol ${item}`}>{modeIcons[item]}</span>{t[item]}
                  </button>
              ))}
            </div>
            <div className="mode-description">{isTimed && <><Icon name="clock" />{t.timed}</>}</div>
            {mode === 'battle' && <label className="battle-size-control">
              <span>{t.rootLength}</span>
              <select value={battleRootSize} onChange={(event) => {
                const size = Number(event.target.value)
                if (size === 2 || size === 3 || size === 4) selectBattleRootSize(size)
              }} aria-label={t.rootLength}>
                {([2, 3, 4] as const).map((size) => <option key={size} value={size}>{size} {language === 'EN' ? 'letters' : 'letras'}</option>)}
              </select>
            </label>}
          </div>

          <div className={`dashboard ${mode === 'battle' && !revealAnswers ? 'dashboard-no-sidebar' : ''}`}>
            <section className="game-column">
              {isTimed && <div className="timer-row">
                <div className="stat-card timer-stat timer-on">
                  <span className="stat-label"><Icon name="clock" />{language === 'EN' ? 'TIME' : 'TIEMPO'}</span>
                  <strong>0:{seconds.toString().padStart(2, '0')}</strong>
                  <span className="stat-caption">{playing ? language === 'EN' ? 'Round in progress' : 'Ronda en curso' : t.start}</span>
                </div>
              </div>}

              <section className="board-card">
                <div className="board-card-header">
                  <div className="root-heading">
                    <div className="root-icon"><Icon name="leaf" /></div>
                    <div><span className="section-kicker">{t.root}</span><div className="root-subtitle">{rootHint}</div></div>
                  </div>
                  <div className="board-actions">
                    {isTimed && !playing ? (
                        <button className="primary-button compact-button" onClick={handleStart} disabled={!canStart}><span className="play-triangle">▶</span>{seconds === 0 || roundComplete ? t.newRound : t.start}</button>
                    ) : null}
                    {isTimed && playing && <button className="stop-button" onClick={handleStop} aria-label={t.stop} title={t.stop}><span className="stop-icon" aria-hidden="true" /></button>}
                  </div>
                </div>

                <div className="progress-track"><span style={{ width: `${resultsHidden ? 0 : progress}%` }} /></div>

                <div className="word-zone">
                  <div className="word-zone-label">
                    <span className="side-indicator">{side === 'left' ? '01' : '02'}</span>
                    <div><strong>{side === 'left' ? t.left : t.right}</strong><span>{side === 'left' ? t.leftHint : t.rightHint}</span></div>
                    <span className={`side-pill ${side}`} aria-label={side === 'left' ? t.leftHooks : t.rightHooks}>{rootSideLabel(side)}</span>
                  </div>

                  <div className="word-trail" aria-live="polite">
                    {resultsHidden || trail.length === 0 ? (
                        <div className="empty-trail">
                          <span className="root-word">{rootSideLabel(side)}</span>
                          <span className="empty-prompt"><span className="sparkle">✦</span>{t.rootHint}</span>
                        </div>
                    ) : (
                        <div className="trail-words">
                          <span className="trail-root">{rootSideLabel(side)}</span>
                          <span className="trail-connector" />
                          {trail.slice(-5).map((word, index) => (
                              <span className="trail-entry" key={`${word}-${index}`}>
                          <span className={`word-tile ${word.length > currentRoot.value.length ? '' : 'root-only'}`}>
                            {word.split('').map((letter, letterIndex) => {
                              const isRoot = word.startsWith(currentRoot.value)
                                  ? letterIndex < currentRoot.value.length
                                  : letterIndex >= word.length - currentRoot.value.length
                              return <span className={isRoot ? 'root-letter' : 'hook-letter'} key={`${letter}-${letterIndex}`}>{keyLabel(letter)}</span>
                            })}
                          </span>
                                {index < Math.min(trail.length, 5) - 1 && <span className="trail-connector short" />}
                        </span>
                          ))}
                        </div>
                    )}
                  </div>

                  <div className={`feedback-line ${feedback === t.incorrect || feedback === t.repeated ? 'feedback-warn' : ''} ${feedback === t.completed ? 'feedback-success' : ''}`} aria-live="polite">
                    {(feedback || mode === 'ninja' && !playing && seconds > 0 && totalHooks === 0) && <>
                      <span className="feedback-mark">{feedback === t.incorrect || feedback === t.repeated ? '!' : feedback === t.completed ? '✓' : '✦'}</span>
                      <span>{mode === 'ninja' && !playing && seconds > 0 && totalHooks === 0 ? t.noHooksOnSide : feedback}</span>
                    </>}
                    {roundComplete && (!resultsHidden || mode !== 'battle') && <button className="next-root-button" onClick={() => {
                      if (mode === 'battle') {
                        nextBattleRoot()
                        return
                      }
                      const currentSizeIndex = sizedRoots.findIndex(({ index }) => index === rootIndex)
                      const nextRoot = sizedRoots[(currentSizeIndex + 1) % sizedRoots.length]
                      if (nextRoot) selectRoot(nextRoot.index)
                    }}>{t.nextRoot}<Icon name="arrow" /></button>}
                  </div>
                </div>

                <div className="keyboard-section">
                  <div className="keyboard-heading">
                    <div><strong>{t.keyboard}</strong><span>{t.keyboardHint}</span></div>
                    {hintsAvailable && (
                        <button className="hints-toggle" type="button" aria-pressed={hintsEnabled} onClick={() => setHintsEnabled((value) => !value)}>
                          {t.hookHints}: {hintsEnabled ? t.hintsOn : t.hintsOff}
                        </button>
                    )}
                  </div>
                  <div className="letter-grid">
                    {keys.map((letter) => {
                      const marker = hintsAvailable && hintsEnabled ? keyboardMarkers.get(letter) : undefined
                      const correct = usedHooks.includes(`${side}:${letter}`)
                      const used = hintsAvailable && hintsEnabled && correct
                      const guesses = (isTimed ? [side] : (['left', 'right'] as const)).flatMap((guessSide) => {
                        const found = usedHooks.includes(`${guessSide}:${letter}`)
                        const wrong = wrongGuesses.includes(`${guessSide}:${letter}`)
                        const unguessed = revealAnswers && !found && currentRoot[guessSide].includes(letter)
                        if (!found && !wrong && !unguessed) return []
                        return [{
                          side: guessSide,
                          status: resultsHidden ? 'submitted' : found ? 'correct' : unguessed ? 'unguessed' : 'wrong',
                          label: `${guessSide === 'left' ? t.leftHooks : t.rightHooks}: ${resultsHidden ? t.submittedGuess : found ? t.correctGuess : unguessed ? t.missedAnswer : t.wrongGuess}`,
                        }]
                      })
                      return (
                          <button
                              key={letter}
                              className={`letter-key ${marker ? 'key-available' : ''} ${used ? 'key-used' : ''}`}
                              onClick={() => submitLetter(letter)}
                              disabled={!playing || roundComplete}
                              aria-label={`${keyLabel(letter)}${guesses.length > 0 ? `, ${guesses.map((guess) => guess.label).join(', ')}` : marker ? `, ${side === 'left' ? t.leftHooks : t.rightHooks}` : ''}`}
                          >
                            {keyLabel(letter)}
                            {guesses.map((guess) => <span key={guess.side} className={`key-dot ${guess.side} ${guess.status}`} aria-hidden="true" />)}
                          </button>
                      )
                    })}
                  </div>
                  <div className="keyboard-footer">
                    {hintsAvailable && hintsEnabled && <>
                      <span><i className="legend-dot green" />{language === 'EN' ? 'Available' : 'Disponible'}</span>
                      <span><i className="legend-dot grey" />{language === 'EN' ? 'Not a hook' : 'No es un gancho'}</span>
                    </>}
                    {resultsHidden ? (
                        <span><i className="legend-dot submitted" />{t.submittedGuess}</span>
                    ) : <>
                      <span><i className="legend-dot correct" />{t.correctGuess}</span>
                      <span><i className="legend-dot wrong" />{t.wrongGuess}</span>
                      {revealAnswers && <span><i className="legend-dot unguessed" />{t.missedAnswer}</span>}
                    </>}
                    {(mode === 'practice' || mode === 'ninja') && <button className="switch-side" onClick={() => setSide((value) => value === 'left' ? 'right' : 'left')} disabled={!canSwitchSide}>
                      {t.switchSide}<span>⇄</span>
                    </button>}
                  </div>
                </div>
              </section>
              <div className="stats-row">
                <div className="stat-card score-stat">
                  <span className="stat-label">{t.score}</span>
                  <strong>{resultsHidden ? '—' : score.toLocaleString(language === 'EN' ? 'en-US' : 'es-ES')}</strong>
                  <span className="stat-caption"><span className="stat-trend">↗</span> {language === 'EN' ? 'Keep it going' : '¡Sigue así!'}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">{t.streak}</span>
                  <strong>{resultsHidden ? '—' : streak}<span className="stat-unit">×</span></strong>
                  <span className="stat-caption"><span className="streak-icon">✳</span> {language === 'EN' ? `Best ${resultsHidden ? '—' : bestStreak}` : `Mejor ${resultsHidden ? '—' : bestStreak}`}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">{t.words}</span>
                  <strong>{resultsHidden ? '—' : wordCount.toString().padStart(2, '0')}</strong>
                  <span className="stat-caption">{resultsHidden ? '—' : misses} {language === 'EN' ? 'misses' : 'errores'}</span>
                </div>
              </div>
            </section>

            {(mode !== 'battle' || revealAnswers) && <aside className="sidebar">
              {(hintsAvailable || revealAnswers) && <section className="side-card hooks-card">
                <div className="side-card-title"><div><span className="section-kicker">{revealAnswers ? t.correctAnswers : t.available}</span><h2>{language === 'EN' ? 'The hook board' : 'Tablero de ganchos'}</h2></div><span className="hook-count">{completedCount}<span>/{totalHooks}</span></span></div>
                <p className="side-description">{language === 'EN' ? 'Every letter is a new way to grow your word.' : 'Cada letra es una nueva forma de ampliar tu palabra.'}</p>
                {(!isTimed || side === 'left') && <div className="hook-list">
                  <div className="hook-list-heading"><span className="hook-direction">←</span><strong aria-label={t.leftHooks}>{rootSideLabel('left')}</strong><span>{currentRoot.left.length - usedHooks.filter((key) => key.startsWith('left:')).length} {t.remaining}</span></div>
                  <div className="hook-letters">
                    {currentRoot.left.map((letter) => {
                      const found = usedHooks.includes(`left:${letter}`)
                      const word = `${letter}${currentRoot.value}`
                      return <span className={`hook-chip ${found ? 'found' : ''}`} key={letter} aria-label={revealAnswers ? `${displayWord(word)}, ${found ? t.foundAnswer : t.missedAnswer}` : !hintsEnabled && !found ? t.hiddenHook : undefined}>{revealAnswers ? displayWord(word) : hintsEnabled || found ? keyLabel(letter) : '?'}{found && <Icon name="check" />}{revealAnswers && !found && <span>({t.missedAnswer})</span>}</span>
                    })}
                  </div>
                </div>}
                {(!isTimed || side === 'right') && <div className="hook-list">
                  <div className="hook-list-heading"><span className="hook-direction">→</span><strong aria-label={t.rightHooks}>{rootSideLabel('right')}</strong><span>{currentRoot.right.length - usedHooks.filter((key) => key.startsWith('right:')).length} {t.remaining}</span></div>
                  <div className="hook-letters">
                    {currentRoot.right.map((letter) => {
                      const found = usedHooks.includes(`right:${letter}`)
                      const word = `${currentRoot.value}${letter}`
                      return <span className={`hook-chip ${found ? 'found' : ''}`} key={letter} aria-label={revealAnswers ? `${displayWord(word)}, ${found ? t.foundAnswer : t.missedAnswer}` : !hintsEnabled && !found ? t.hiddenHook : undefined}>{revealAnswers ? displayWord(word) : hintsEnabled || found ? keyLabel(letter) : '?'}{found && <Icon name="check" />}{revealAnswers && !found && <span>({t.missedAnswer})</span>}</span>
                    })}
                  </div>
                </div>}
                <div className="root-progress"><div className="progress-copy"><span>{t.solved}</span><strong>{Math.round(progress)}%</strong></div><div className="progress-track"><span style={{ width: `${progress}%` }} /></div></div>
              </section>}

              {mode !== 'battle' && <section className="side-card root-card">
                <div className="side-card-title"><div><span className="section-kicker">{t.roots} ({sizedRoots.length})</span><h2>{language === 'EN' ? 'Pick a new starting point' : 'Elige un nuevo comienzo'}</h2></div></div>
                <div className="root-size-control">
                  <span className="section-kicker">{t.rootLength}</span>
                  <div className="root-size-options" role="group" aria-label={t.rootLength}>
                    {([2, 3, 4] as const).map((size) => (
                        <button key={size} className={`root-size-option ${rootSize === size ? 'active' : ''}`} type="button" onClick={() => selectRootSize(size)} aria-pressed={rootSize === size}>
                          {size} {language === 'EN' ? 'letters' : 'letras'}
                        </button>
                    ))}
                  </div>
                </div>
                <nav className="root-index" aria-label={t.rootIndex}>
                  {rootGroups.map(({ letter }) => (
                      <button key={letter} type="button" onClick={() => scrollToRootLetter(letter)} aria-label={`${t.rootIndex}: ${keyLabel(letter)}`}>{keyLabel(letter)}</button>
                  ))}
                </nav>
                <div className="root-picker" ref={rootPickerRef} key={rootSize}>
                  {rootGroups.map(({ letter, entries }) => (
                      <Fragment key={letter}>
                        <div className="root-group-heading" data-root-letter={letter} tabIndex={-1}>{keyLabel(letter)}</div>
                        {entries.map(({ root, index }) => {
                          const completed = completedRoots.includes(root.value)
                          const attempted = attemptedRoots.includes(root.value)
                          const pending = resultsHidden && rootIndex === index && usedHooks.length + wrongGuesses.length > 0
                          return (
                              <button key={root.value} className={`root-chip ${rootIndex === index ? 'active' : ''}`} onClick={() => selectRoot(index)} aria-pressed={rootIndex === index}>
                                {displayWord(root.value)}
                                {attempted && <span className={`root-chip-dot ${pending ? 'submitted' : completed ? 'completed' : 'attempted'}`} aria-label={pending ? t.submittedGuess : completed ? 'All hooks discovered' : 'Attempted'} />}
                              </button>
                          )
                        })}
                      </Fragment>
                  ))}
                </div>
                <div className="root-card-footer"><span className="round-leaf"><Icon name="leaf" /></span><span>{language === 'EN' ? 'A new root, a new possibility.' : 'Una raíz nueva, una posibilidad nueva.'}</span></div>
              </section>}

              {hintsAvailable && <section className="tip-card">
                <div className="tip-graphic"><span>✦</span><i /><b /></div>
                <div className="tip-content"><span className="section-kicker">{t.hint}</span><strong>{language === 'EN' ? 'Look both ways.' : 'Mira a ambos lados.'}</strong><p>{language === 'EN' ? 'Some roots open up on the left. Others have a surprise waiting on the right.' : 'Algunas raíces se amplían a la izquierda. Otras esconden una sorpresa a la derecha.'}</p></div>
              </section>}
            </aside>}
          </div>
          <footer className="page-footer"><span>HOOKS<span className="footer-dot">.</span></span><span>{language === 'EN' ? 'Made for curious minds.' : 'Hecho para mentes curiosas.'}</span></footer>
        </main>

        {modal && (
            <div className="modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setModal(null) }}>
              <section className="modal-card" role="dialog" aria-modal="true" aria-labelledby="modal-title">
                <button className="modal-close" onClick={() => setModal(null)} aria-label={language === 'EN' ? 'Close dialog' : 'Cerrar diálogo'}>×</button>
                <div className="modal-icon"><Icon name="spark" /></div>
                <span className="section-kicker">{t.howToPlay}</span>
                <h2 id="modal-title">{t.howTitle}</h2>
                <p>{t.howBody}</p>
                <button className="primary-button modal-action" onClick={() => setModal(null)}>{language === 'EN' ? 'Got it' : 'Entendido'}<Icon name="arrow" /></button>
              </section>
            </div>
        )}
      </div>
  )
}

export default App

import { useEffect, useMemo, useState } from 'react'
import hooksLogo from '../app/src/main/res/drawable/hooks_logo.png'
import { roots } from './roots'

type Side = 'left' | 'right'
type Mode = 'practice' | 'game' | 'ninja' | 'battle'
type Language = 'EN' | 'ES'

const copy = {
  EN: {
    tagline: 'Wordplay, one letter at a time.',
    rooms: 'Rooms',
    howToPlay: 'How to play',
    practice: 'Practice',
    game: 'Game',
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
    available: 'Available hooks',
    leftHooks: 'BEFORE',
    rightHooks: 'AFTER',
    roots: 'Choose a root',
    newRound: 'New round',
    switchSide: 'Switch side',
    nextRoot: 'Next root',
    pause: 'Pause',
    resume: 'Resume',
    start: 'Start round',
    rootHint: 'A fresh root is waiting',
    trail: 'Your word trail',
    trailHint: 'Each new word starts with the same root.',
    welcome: 'Ready when you are',
    correct: 'Nice hook! Keep going.',
    incorrect: 'Not quite — try another letter.',
    repeated: 'You already found that hook.',
    completed: 'All hooks found. Brilliant work!',
    timeUp: "Time's up. What a run!",
    howTitle: 'The art of the hook',
    howBody: 'Start with the highlighted root. Add one letter before it or after it to make a word. Find every hook on one side, then keep going on the other.',
    roomTitle: 'Find your people',
    roomBody: 'Live rooms let you play Hooks together. Room discovery and sign-in need the original Firebase service, which is not connected to this web preview yet.',
    roomAction: 'Back to the board',
    timed: '40 second round',
    localOnly: 'Solo preview',
    hint: 'Hint',
    modeNote: 'Choose a mode to make it your own.',
    solved: 'FOUND',
    remaining: 'left to find',
  },
  ES: {
    tagline: 'Juega con palabras, letra a letra.',
    rooms: 'Salas',
    howToPlay: 'Cómo jugar',
    practice: 'Práctica',
    game: 'Juego',
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
    available: 'Ganchos disponibles',
    leftHooks: 'ANTES',
    rightHooks: 'DESPUÉS',
    roots: 'Elige una raíz',
    newRound: 'Nueva ronda',
    switchSide: 'Cambiar lado',
    nextRoot: 'Siguiente raíz',
    pause: 'Pausar',
    resume: 'Continuar',
    start: 'Empezar ronda',
    rootHint: 'Una nueva raíz te espera',
    trail: 'Tu camino de palabras',
    trailHint: 'Cada palabra nueva empieza con la misma raíz.',
    welcome: 'Cuando quieras',
    correct: '¡Buen gancho! Sigue así.',
    incorrect: 'No exactamente. Prueba otra letra.',
    repeated: 'Ya encontraste ese gancho.',
    completed: '¡Encontraste todos los ganchos!',
    timeUp: 'Se acabó el tiempo. ¡Qué buena ronda!',
    howTitle: 'El arte del gancho',
    howBody: 'Empieza con la raíz resaltada. Añade una letra antes o después para crear una palabra. Encuentra todos los ganchos de un lado y continúa por el otro.',
    roomTitle: 'Encuentra tu grupo',
    roomBody: 'Las salas te permiten jugar Hooks en compañía. La búsqueda de salas y el inicio de sesión necesitan el servicio Firebase original, que aún no está conectado a esta vista web.',
    roomAction: 'Volver al tablero',
    timed: 'Ronda de 40 segundos',
    localOnly: 'Vista individual',
    hint: 'Pista',
    modeNote: 'Elige un modo y hazlo tuyo.',
    solved: 'ENCONTRADAS',
    remaining: 'por encontrar',
  },
}

const alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split('')
const timedModes: Mode[] = ['ninja', 'battle']
const modeIcons: Record<Mode, string> = {
  practice: '✳',
  game: '▰',
  ninja: '◈',
  battle: '⚔',
}

function Icon({ name }: { name: 'spark' | 'clock' | 'arrow' | 'help' | 'users' | 'leaf' | 'check' | 'refresh' }) {
  const common = { width: 18, height: 18, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true as const }

  if (name === 'clock') return <svg {...common}><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></svg>
  if (name === 'arrow') return <svg {...common}><path d="M5 12h14M13 6l6 6-6 6" /></svg>
  if (name === 'help') return <svg {...common}><circle cx="12" cy="12" r="9" /><path d="M9.6 9a2.5 2.5 0 0 1 4.8 1c0 1.8-2.4 2-2.4 4M12 17.5v.1" /></svg>
  if (name === 'users') return <svg {...common}><path d="M16 20v-1.6a3.4 3.4 0 0 0-3.4-3.4H6.4A3.4 3.4 0 0 0 3 18.4V20M9.5 11a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7ZM17 4.2a3.5 3.5 0 0 1 0 6.7M21 20v-1.6a3.4 3.4 0 0 0-2.5-3.3" /></svg>
  if (name === 'check') return <svg {...common}><path d="m5 12 4 4L19 6" /></svg>
  if (name === 'refresh') return <svg {...common}><path d="M20 7v5h-5M4 17v-5h5" /><path d="M5.5 9A7 7 0 0 1 17 6.5L20 12M4 12l3 5.5A7 7 0 0 0 18.5 15" /></svg>
  if (name === 'leaf') return <svg {...common}><path d="M20 4c-8 0-14 3.8-14 10a6 6 0 0 0 6 6c6.2 0 8-7 8-16Z" /><path d="M4 21c3-5 7-8 12-11" /></svg>
  return <svg {...common}><path d="m12 3 1.7 5.3L19 10l-5.3 1.7L12 17l-1.7-5.3L5 10l5.3-1.7L12 3ZM19 15l.9 2.1L22 18l-2.1.9L19 21l-.9-2.1L16 18l2.1-.9L19 15Z" /></svg>
}

function App() {
  const [language, setLanguage] = useState<Language>('EN')
  const [mode, setMode] = useState<Mode>('practice')
  const [rootIndex, setRootIndex] = useState(0)
  const [side, setSide] = useState<Side>('left')
  const [usedHooks, setUsedHooks] = useState<string[]>([])
  const [trail, setTrail] = useState<string[]>([])
  const [score, setScore] = useState(0)
  const [streak, setStreak] = useState(0)
  const [bestStreak, setBestStreak] = useState(0)
  const [misses, setMisses] = useState(0)
  const [message, setMessage] = useState('')
  const [playing, setPlaying] = useState(true)
  const [seconds, setSeconds] = useState(40)
  const [modal, setModal] = useState<'help' | 'rooms' | null>(null)

  const t = copy[language]
  const currentRoot = roots[rootIndex]
  const totalHooks = currentRoot.left.length + currentRoot.right.length
  const completedCount = usedHooks.length
  const roundComplete = completedCount === totalHooks
  const isTimed = timedModes.includes(mode)
  const wordCount = trail.length + 1
  const progress = (completedCount / totalHooks) * 100

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
    setSeconds(40)
    setPlaying(!timedModes.includes(nextMode))
    setMessage('')
  }

  const resetRound = (nextRootIndex = rootIndex) => {
    setRootIndex(nextRootIndex)
    setUsedHooks([])
    setTrail([])
    setSide('left')
    setSeconds(40)
    setPlaying(!isTimed)
    setMessage(t.welcome)
    setStreak(0)
  }

  const submitLetter = (letter: string) => {
    if (!alphabet.includes(letter) || !playing || roundComplete) return
    const possibleLetters = side === 'left' ? currentRoot.left : currentRoot.right
    const key = `${side}:${letter}`
    if (!possibleLetters.includes(letter)) {
      setMessage(usedHooks.includes(key) ? t.repeated : t.incorrect)
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
      setMessage(t.completed)
      setPlaying(false)
    } else if (sideDone && side === 'left') {
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
      if (/^[a-z]$/i.test(event.key)) submitLetter(event.key.toUpperCase())
      if (event.key === 'Backspace' && trail.length > 0) {
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
    if (seconds === 0 || roundComplete) resetRound()
    setPlaying(true)
    setMessage('')
  }

  const rootHint = currentRoot.hint

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#" aria-label="Hooks home">
          <span className="brand-mark"><img src={hooksLogo} alt="" /></span>
          <span className="brand-name">hooks<span>.</span><small>{t.tagline}</small></span>
        </a>
        <div className="topbar-actions">
          <button className="text-button room-button" onClick={() => setModal('rooms')}><Icon name="users" />{t.rooms}</button>
          <button className="icon-button help-button" aria-label={t.howToPlay} onClick={() => setModal('help')}><Icon name="help" /></button>
          <span className="topbar-divider" />
          <button className="language-toggle" onClick={() => setLanguage((value) => value === 'EN' ? 'ES' : 'EN')} aria-label="Change language">
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
          <div className="player-note">
            <div className="note-icon"><Icon name="spark" /></div>
            <div><strong>{t.localOnly}</strong><span>{t.modeNote}</span></div>
          </div>
        </div>

        <div className="mode-row">
          <div className="mode-switch" role="group" aria-label="Game mode">
            {(['practice', 'game', 'ninja', 'battle'] as Mode[]).map((item) => (
              <button key={item} className={`mode-option ${mode === item ? 'selected' : ''}`} onClick={() => selectMode(item)} aria-pressed={mode === item}>
                <span className={`mode-symbol ${item}`}>{modeIcons[item]}</span>{t[item]}
              </button>
            ))}
          </div>
          <div className="mode-description">{isTimed && <><Icon name="clock" />{t.timed}</>}</div>
        </div>

        <div className="dashboard">
          <section className="game-column">
            <div className="stats-row">
              <div className="stat-card score-stat">
                <span className="stat-label">{t.score}</span>
                <strong>{score.toLocaleString(language === 'EN' ? 'en-US' : 'es-ES')}</strong>
                <span className="stat-caption"><span className="stat-trend">↗</span> {language === 'EN' ? 'Keep it going' : '¡Sigue así!'}</span>
              </div>
              <div className="stat-card">
                <span className="stat-label">{t.streak}</span>
                <strong>{streak}<span className="stat-unit">×</span></strong>
                <span className="stat-caption"><span className="streak-icon">✳</span> {language === 'EN' ? `Best ${bestStreak}` : `Mejor ${bestStreak}`}</span>
              </div>
              <div className="stat-card">
                <span className="stat-label">{t.words}</span>
                <strong>{wordCount.toString().padStart(2, '0')}</strong>
                <span className="stat-caption">{misses} {language === 'EN' ? 'misses' : 'errores'}</span>
              </div>
              <div className={`stat-card timer-stat ${isTimed ? 'timer-on' : ''}`}>
                <span className="stat-label"><Icon name="clock" />{language === 'EN' ? 'TIME' : 'TIEMPO'}</span>
                <strong>{isTimed ? `0:${seconds.toString().padStart(2, '0')}` : '— —'}</strong>
                <span className="stat-caption">{isTimed ? (playing ? language === 'EN' ? 'Round in progress' : 'Ronda en curso' : t.start) : t.timed}</span>
              </div>
            </div>

            <section className="board-card">
              <div className="board-card-header">
                <div className="root-heading">
                  <div className="root-icon"><Icon name="leaf" /></div>
                  <div><span className="section-kicker">{t.root}</span><div className="root-subtitle">{rootHint}</div></div>
                </div>
                <div className="board-actions">
                  {isTimed && !playing ? (
                    <button className="primary-button compact-button" onClick={handleStart}><span className="play-triangle">▶</span>{seconds === 0 ? t.newRound : t.start}</button>
                  ) : (
                    <button className="subtle-button" onClick={() => resetRound()}><Icon name="refresh" />{t.newRound}</button>
                  )}
                  {isTimed && playing && <button className="pause-button" onClick={() => setPlaying(false)} aria-label={t.pause}>Ⅱ</button>}
                </div>
              </div>

              <div className="progress-track"><span style={{ width: `${progress}%` }} /></div>

              <div className="word-zone">
                <div className="word-zone-label">
                  <span className="side-indicator">{side === 'left' ? '01' : '02'}</span>
                  <div><strong>{side === 'left' ? t.left : t.right}</strong><span>{side === 'left' ? t.leftHint : t.rightHint}</span></div>
                  <span className={`side-pill ${side}`}>{side === 'left' ? t.leftHooks : t.rightHooks}</span>
                </div>

                <div className="word-trail" aria-live="polite">
                  {trail.length === 0 ? (
                    <div className="empty-trail">
                      <span className="root-word">{currentRoot.value}</span>
                      <span className="empty-prompt"><span className="sparkle">✦</span>{t.rootHint}</span>
                    </div>
                  ) : (
                    <div className="trail-words">
                      <span className="trail-root">{currentRoot.value}</span>
                      <span className="trail-connector" />
                      {trail.slice(-5).map((word, index) => (
                        <span className="trail-entry" key={`${word}-${index}`}>
                          <span className={`word-tile ${word.length > currentRoot.value.length ? '' : 'root-only'}`}>
                            {word.split('').map((letter, letterIndex) => {
                              const isRoot = word.startsWith(currentRoot.value)
                                ? letterIndex < currentRoot.value.length
                                : letterIndex >= word.length - currentRoot.value.length
                              return <span className={isRoot ? 'root-letter' : 'hook-letter'} key={`${letter}-${letterIndex}`}>{letter}</span>
                            })}
                          </span>
                          {index < Math.min(trail.length, 5) - 1 && <span className="trail-connector short" />}
                        </span>
                      ))}
                    </div>
                  )}
                </div>

                <div className={`feedback-line ${message === t.incorrect || message === t.repeated ? 'feedback-warn' : ''} ${message === t.completed ? 'feedback-success' : ''}`} aria-live="polite">
                  <span className="feedback-mark">{message === t.incorrect || message === t.repeated ? '!' : message === t.completed ? '✓' : '✦'}</span>
                  <span>{message || t.welcome}</span>
                  {roundComplete && <button className="next-root-button" onClick={() => resetRound((rootIndex + 1) % roots.length)}>{t.nextRoot}<Icon name="arrow" /></button>}
                </div>
              </div>

              <div className="keyboard-section">
                <div className="keyboard-heading"><div><strong>{t.keyboard}</strong><span>{t.keyboardHint}</span></div><span className="keyboard-status"><span className="status-dot" />{t.available}</span></div>
                <div className="letter-grid">
                  {alphabet.map((letter) => {
                    const marker = keyboardMarkers.get(letter)
                    const used = usedHooks.some((hook) => hook.endsWith(`:${letter}`))
                    return (
                      <button
                        key={letter}
                        className={`letter-key ${marker ? 'key-available' : ''} ${used ? 'key-used' : ''}`}
                        onClick={() => submitLetter(letter)}
                        disabled={!playing || roundComplete}
                        aria-label={`${letter}${marker ? `, ${marker} hook` : ''}`}
                      >
                        {letter}
                        {marker && <span className="key-dot" />}
                      </button>
                    )
                  })}
                </div>
                <div className="keyboard-footer">
                  <span><i className="legend-dot green" />{language === 'EN' ? 'Available' : 'Disponible'}</span>
                  <span><i className="legend-dot grey" />{language === 'EN' ? 'Not a hook' : 'No es un gancho'}</span>
                  <button className="switch-side" onClick={() => setSide((value) => value === 'left' ? 'right' : 'left')} disabled={roundComplete}>
                    {t.switchSide}<span>⇄</span>
                  </button>
                </div>
              </div>
            </section>
          </section>

          <aside className="sidebar">
            <section className="side-card hooks-card">
              <div className="side-card-title"><div><span className="section-kicker">{t.available}</span><h2>{language === 'EN' ? 'The hook board' : 'Tablero de ganchos'}</h2></div><span className="hook-count">{completedCount}<span>/{totalHooks}</span></span></div>
              <p className="side-description">{language === 'EN' ? 'Every letter is a new way to grow your word.' : 'Cada letra es una nueva forma de ampliar tu palabra.'}</p>
              <div className="hook-list">
                <div className="hook-list-heading"><span className="hook-direction">←</span><strong>{t.leftHooks}</strong><span>{currentRoot.left.length - usedHooks.filter((key) => key.startsWith('left:')).length} {t.remaining}</span></div>
                <div className="hook-letters">
                  {currentRoot.left.map((letter) => <span className={`hook-chip ${usedHooks.includes(`left:${letter}`) ? 'found' : ''}`} key={letter}>{letter}{usedHooks.includes(`left:${letter}`) && <Icon name="check" />}</span>)}
                </div>
              </div>
              <div className="hook-list">
                <div className="hook-list-heading"><span className="hook-direction">→</span><strong>{t.rightHooks}</strong><span>{currentRoot.right.length - usedHooks.filter((key) => key.startsWith('right:')).length} {t.remaining}</span></div>
                <div className="hook-letters">
                  {currentRoot.right.map((letter) => <span className={`hook-chip ${usedHooks.includes(`right:${letter}`) ? 'found' : ''}`} key={letter}>{letter}{usedHooks.includes(`right:${letter}`) && <Icon name="check" />}</span>)}
                </div>
              </div>
              <div className="root-progress"><div className="progress-copy"><span>{t.solved}</span><strong>{Math.round(progress)}%</strong></div><div className="progress-track"><span style={{ width: `${progress}%` }} /></div></div>
            </section>

            <section className="side-card root-card">
              <div className="side-card-title"><div><span className="section-kicker">{t.roots} ({roots.length})</span><h2>{language === 'EN' ? 'Pick a new starting point' : 'Elige un nuevo comienzo'}</h2></div></div>
              <div className="root-picker">
                {roots.map((root, index) => <button key={root.value} className={`root-chip ${rootIndex === index ? 'active' : ''}`} onClick={() => resetRound(index)} aria-pressed={rootIndex === index}>{root.value}{rootIndex === index && <span className="root-chip-dot" />}</button>)}
              </div>
              <div className="root-card-footer"><span className="round-leaf"><Icon name="leaf" /></span><span>{language === 'EN' ? 'A new root, a new possibility.' : 'Una raíz nueva, una posibilidad nueva.'}</span></div>
            </section>

            <section className="tip-card">
              <div className="tip-graphic"><span>✦</span><i /><b /></div>
              <div className="tip-content"><span className="section-kicker">{t.hint}</span><strong>{language === 'EN' ? 'Look both ways.' : 'Mira a ambos lados.'}</strong><p>{language === 'EN' ? 'Some roots open up on the left. Others have a surprise waiting on the right.' : 'Algunas raíces se amplían a la izquierda. Otras esconden una sorpresa a la derecha.'}</p></div>
            </section>
          </aside>
        </div>
        <footer className="page-footer"><span>HOOKS<span className="footer-dot">.</span></span><span>{language === 'EN' ? 'Made for curious minds.' : 'Hecho para mentes curiosas.'}</span><span className="footer-right"><Icon name="leaf" />{t.localOnly}</span></footer>
      </main>

      {modal && (
        <div className="modal-backdrop" onMouseDown={(event) => { if (event.target === event.currentTarget) setModal(null) }}>
          <section className="modal-card" role="dialog" aria-modal="true" aria-labelledby="modal-title">
            <button className="modal-close" onClick={() => setModal(null)} aria-label={language === 'EN' ? 'Close dialog' : 'Cerrar diálogo'}>×</button>
            <div className={`modal-icon ${modal}`}><Icon name={modal === 'help' ? 'spark' : 'users'} /></div>
            <span className="section-kicker">{modal === 'help' ? t.howToPlay : t.rooms}</span>
            <h2 id="modal-title">{modal === 'help' ? t.howTitle : t.roomTitle}</h2>
            <p>{modal === 'help' ? t.howBody : t.roomBody}</p>
            <button className="primary-button modal-action" onClick={() => setModal(null)}>{modal === 'help' ? language === 'EN' ? 'Got it' : 'Entendido' : t.roomAction}<Icon name="arrow" /></button>
          </section>
        </div>
      )}
    </div>
  )
}

export default App

# Project Memory & Design Guidelines

**Project Name**: `watch-anime-app`
**Tech Stack**: Angular 17+ (standalone components, `@if`/`@for` control flow), Tailwind CSS, Vanilla CSS (minimal, mostly Tailwind).

## 🎨 Design Philosophy
- **Theme**: Ultra-premium, cinematic dark mode.
- **Color Palette**: 
  - Deep Blacks: `#0a0a0c`, `#161619`, `#1a1a1f`, `#121214`
  - Accents: Vibrant Purple (`purple-500`, `purple-600`), Yellow for scores.
- **Aesthetics**: Glassmorphism (`backdrop-blur-md`, `bg-white/5`), deep drop shadows (`shadow-2xl`, `shadow-purple-500/20`), smooth micro-animations (`transition-all duration-300`, `group-hover`), and modern typography tracking.

## 🏗️ Key Components & Architecture

### 1. Dashboard (`dashboard.component`)
- **Hero Section**: Huge title, professional subtitle, and a massive, prominent Search Bar floating centrally over the grid (`z-50`).
- **Anime Grid**: Fully responsive grid. Cards feature hover-state image scaling, floating absolute badges (Rating/Score), and a three-line clamped description.
- **Loading State**: Highly professional, pulsing skeleton UI that perfectly mirrors the actual data layout.

### 2. Anime Detail Page (`anime-detail.component`)
- **Layout**: 3-Column fluid layout that is strictly horizontal (`h-screen`) on Desktop, but stacks vertically gracefully on Mobile (`min-h-screen flex-col`).
- **Ambilight Effect**: The video player has a cinematic backdrop created by rendering the anime's background image with `blur-[100px]`, scaled up, and overlaid with a dark gradient.
- **Episode Grid**: Custom grid with a hover tooltip that reveals the full episode title cleanly.
- **Server Selector**: Compact, perfectly responsive block below the video player that houses the active episode indicator, SUB/DUB buttons, and a dynamic "Next Episode" button.

### 3. Global Search (`search.component`)
- **Logic**: Uses a debounced input (400ms delay) to fetch data from `localhost:8080/loadAnime`. 
- **Variants**: Takes an `@Input() variant: 'normal' | 'large'`.
  - `normal`: Sleek, compact pill-shape. Used in top navigation bars.
  - `large`: Massive, prominent hero search bar with an integrated search button. Used in the dashboard.
- **Responsiveness**: Fully fluid widths, smart absolute positioning using `top-1/2 -translate-y-1/2` to mathematically center icons, and touch-target optimized for mobile.

## 📱 Mobile Responsiveness Rules
- Never compromise the desktop view. Use Tailwind's `md:` and `lg:` prefixes heavily.
- On small screens, convert complex horizontal flex containers to vertical stacks (`flex-col`).
- Hide non-essential text on buttons (e.g., changing "Next Episode" to "Next", or "Back to Home" to "Back" using `hidden sm:inline`).
- Allow lengthy titles to wrap elegantly using `line-clamp-2` and `leading-tight`.

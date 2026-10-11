---
name: Everyday Value H5 Commerce
colors:
  surface: '#f7f9ff'
  surface-dim: '#d6dae2'
  surface-bright: '#f7f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f0f4fc'
  surface-container: '#eaeef6'
  surface-container-high: '#e4e8f0'
  surface-container-highest: '#dee3eb'
  on-surface: '#171c22'
  on-surface-variant: '#414753'
  inverse-surface: '#2c3137'
  inverse-on-surface: '#edf1f9'
  outline: '#717784'
  outline-variant: '#c1c6d5'
  surface-tint: '#005db7'
  primary: '#0059af'
  on-primary: '#ffffff'
  primary-container: '#0071dc'
  on-primary-container: '#f9f9ff'
  inverse-primary: '#a9c7ff'
  secondary: '#795900'
  on-secondary: '#ffffff'
  secondary-container: '#fcc01d'
  on-secondary-container: '#6c5000'
  tertiary: '#b8000c'
  on-tertiary: '#ffffff'
  tertiary-container: '#e70213'
  on-tertiary-container: '#fff8f7'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d6e3ff'
  primary-fixed-dim: '#a9c7ff'
  on-primary-fixed: '#001b3d'
  on-primary-fixed-variant: '#00468c'
  secondary-fixed: '#ffdf9f'
  secondary-fixed-dim: '#f9bd19'
  on-secondary-fixed: '#261a00'
  on-secondary-fixed-variant: '#5b4300'
  tertiary-fixed: '#ffdad5'
  tertiary-fixed-dim: '#ffb4aa'
  on-tertiary-fixed: '#410001'
  on-tertiary-fixed-variant: '#930007'
  background: '#f7f9ff'
  on-background: '#171c22'
  surface-variant: '#dee3eb'
typography:
  display-hero:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '800'
    lineHeight: 34px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '700'
    lineHeight: 26px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '700'
    lineHeight: 22px
  price-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '800'
    lineHeight: 24px
  price-currency:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '700'
    lineHeight: 14px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  badge-label:
    fontFamily: Plus Jakarta Sans
    fontSize: 10px
    fontWeight: '700'
    lineHeight: 12px
  tab-label:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 0.5rem
  gutter-md: 0.75rem
  margin: 0.75rem
  margin-sm: 0.5rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.75rem
  space-lg: 1rem
  space-xl: 1.5rem
---

## Brand & Style

The design system embodies the warmth, accessibility, and high-efficiency standard of modern mobile retail web apps (H5 / WeChat Mini-Program ecosystem). Designed for high-frequency grocery and daily essentials shoppers, the aesthetic is straightforward, vibrant, and dependable. It avoids high-concept abstraction in favor of rapid visual scanning, tactile clarity, and trustworthy value signaling.

### Visual Movement: Modern Retail Functionalism
- **Base Style:** Corporate / Modern with energetic commercial retail touches.
- **Card-Centric Modularity:** Clean, floating white panels on a soft cool-tinted ground define categorical zones, flash deals, and SKU lists.
- **Urgency & Value Signifiers:** High-chroma warmth (price red, spark yellow, and energetic orange) directs focus immediately to discounts, cart triggers, and delivery commitments.
- **Micro-tactile Touchpoints:** Prominent circular stepper controls and quick-add floating action triggers optimize thumb reach and seamless impulse additions.

## Colors

The palette balances foundational brand trust with retail conversion drivers:

- **Primary (`#0071DC` / Deep Blue `#004F98`):** Used for top-level search inputs, navigation tabs, primary submission buttons, and official brand badging. Establishes institutional reliability.
- **Secondary (`#FFC220` Spark Yellow & `#FF6B00` Warm Amber):** Delivers VIP member benefits ("Savings Card"), promotional headers, ranking ribbons, and loyalty incentives.
- **Tertiary (`#E60012` Retail Price Red):** High-priority currency text, percentage-off badges, countdown indicators, and lightning-deal markers.
- **Neutral Grounds (`#F4F6F8` Canvas, `#FFFFFF` Card Surface):** Maintains high contrast and visual separation between complex multi-column SKU feeds.
- **Text & Borders:** `#2A2F35` for primary headings, `#6E7781` for subtext/specifications, and `#E5E9EE` for subtle card dividers.

## Typography

The typographic hierarchy prioritizes rapid item comprehension and numerical price clarity on small handheld viewports.

- **System Compatibility:** While `Plus Jakarta Sans` is specified for Latin characters and numerical price displays, CJK fallbacks defer seamlessly to `PingFang SC`, `Hiragino Sans GB`, and `Microsoft YaHei`.
- **Price Treatment:** The currency symbol (`¥`) renders at 60-70% of the integer size with a baseline alignment flush to the price floor. The decimal and cent numbers drop slightly in scale to maintain visual hierarchy.
- **Product Titles:** Limited to 2-line line clamps (`body-md`), preserving line height regularity across grid tiles.

## Layout & Spacing

Mobile H5 screens demand strict density optimization:

- **Mobile Viewport Structure:** Standard 375px–414px fluid base. Maximum content width for tablet scaling is capped at 540px, centered with a responsive ambient backdrop.
- **Horizontal Rhythm:** Standard page margins are `12px` (`0.75rem`), accommodating two-column product feeds with `8px` (`0.5rem`) central gutters.
- **Vertical Spacing:** Vertical modules (e.g., banner carousel to category icon row, category icons to promotional matrix) employ a standardized `10px`–`12px` stack distance to balance information density and tap targets.
- **Category Navigation Shell:** When displaying split layouts (left-rail category vertical menu, right-rail product feed), the left rail occupies fixed `84px` width, while the right SKU grid utilizes a fluid single or two-column sub-grid.

## Elevation & Depth

To preserve rendering performance on lower-tier mobile browsers and web views, the design system minimizes deep blurs in favor of crisp boundaries and low-contrast ambient drops:

- **Base Layer (Level 0):** Canvas background `#F4F6F8` / `#F7F8FA`.
- **Card Modules (Level 1):** Solid `#FFFFFF` surfaces with subtle, crisp shadows: `0px 2px 8px rgba(0, 30, 80, 0.04)`. Low-contrast border (`1px solid rgba(0, 0, 0, 0.03)`) protects card geometry against light backgrounds.
- **Sticky / Floating Headers (Level 2):** Top search bars and sticky tab strips feature `0px 4px 12px rgba(0, 20, 60, 0.06)`, maintaining clear separation over scrolling product feeds.
- **Bottom Navigation & Persistent Bars (Level 3):** Standard `0px -2px 10px rgba(0, 0, 0, 0.05)` with `#FFFFFF` solid fill to anchor checkout and global cart triggers.

## Shapes

The geometric identity relies on softened, approachable rectangles that suggest friendly consumer packaging:

- **Product & Section Cards:** Standard corner radius is `12px` (`rounded-lg`), expanding to `16px` (`rounded-xl`) on hero promotional containers and coupon modules.
- **Buttons & Tags:** Primary action buttons (`Add to Cart`, `Search`, `Open VIP`) utilize fully rounded pill silhouettes (`9999px`) to invite thumb taps. Small deal badges and sub-category chips use `4px`–`6px` radius for structural compactness.
- **Interactive Add/Remove Steppers:** Symmetrical circular touch targets (`32px` diameter) for quick single-hand item increments.

## Components

### Buttons
- **Primary Cart Action:** Blue circular icon button (`32px × 32px`, `#0071DC`) featuring a crisp white plus sign (`+`).
- **Submit / Full-Width Buttons:** Pill-shaped (`44px` height), `#0071DC` fill, white bold label. Secondary styles employ orange-to-red gradients (`#FF6B00` to `#E60012`) for flash deals.

### Badges & Pill Chips
- **Discount Badges:** Solid `#E60012` fill with white text, positioned at the top-left of thumbnail images or inline preceding product names.
- **Ranking / Authority Tags:** Warm gold `#FFC220` or translucent amber background with dark brown text (e.g., `TOP 1`).
- **Filter Chips:** Light grey `#F0F2F5` in resting state; switches to `#EBF4FD` with `#0071DC` text and 1px border when active.

### Product Card (Feed & List)
- **Grid Layout (2-Column):** Solid white surface, `12px` border radius, upper 1:1 aspect ratio image container, dual-line title, subtitle specs, bottom price lockup flanked by the circular blue quick-add button.
- **List Layout (Horizontal SKU):** Left thumbnail (`90px × 90px`), right info stack showing title, sales tag, pricing (`price-lg`), and quantity stepper.

### Search Bar & Sticky Header
- Pill container (`36px` height) with `#F2F4F7` background, embedded magnifying glass icon, and right-aligned blue search button or QR scanner trigger.

### Bottom Navigation Tab Bar
- Persistent `50px` height container with 4–5 keys (Home, Category, Cart with quantity bubble badge, My Profile). Active icons fill in Walmart Blue (`#0071DC`) with matching `11px` label.
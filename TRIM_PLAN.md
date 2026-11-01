# Trim plan: SD Maid SE, cleaner-only build

Keep: CorpseFinder, SystemCleaner, AppCleaner (the "system cleaner" caches tool), AppControl, Stats.
Trim: Deduplicator, Squeezer, Analyzer, Swiper, Scheduler.

## Phase 1: hide (done in this branch)

Modules still compile; nothing trimmed is reachable from the UI.

- `DashboardViewModel`: trimmed card types fall through to a no-op in the card loop.
- `SettingsScreen`: removed the Deduplicator, Squeezer, Swiper, Analyzer and Scheduler entries.
- `DashboardCardConfigViewModel`: trimmed types are not listed or reordered (entries preserved in the stored config).
- One-click: Deduplicator forced off in `DashboardMainActionEngine` and `OneTapCleaner`; its toggle removed from `OneClickOptionsDialog`.
- `.github/workflows/build-apk.yml`: builds `app:assembleFossDebug` and uploads the APK.

Known leftovers (harmless, removed in phase 2): the hidden tools' flows still run inside `DashboardViewModel`
(Swiper DB query, Analyzer, Scheduler state), widget and `LowSpaceMonitor` still read `AnalyzerSettings`, and
unit tests that assert Deduplicator one-click behaviour will fail (`OneTapCleanerTest`, `DashboardMainActionEngineTest`).

## Phase 2: delete (done in one pass, unverified until CI is green)

Removed the Deduplicator, Squeezer, Analyzer, Swiper and Scheduler modules (Gradle, kover, manifest receiver) and every
reference in the app module: dashboard view model, card flows, hero/main-action engine, events, dialogs, screen and tour,
settings, one-tap cleaner, Hilt bindings.

Behaviour changes to be aware of:

- Low-storage notification: its settings now live in `LowStorageSettings` (new datastore), but its toggle was in the
  Analyzer settings screen, so it can no longer be switched on. Previously saved threshold/toggle values are not carried over.
- Widget storage tap and the "open analyzer" shortcut now just open the dashboard.
- Exclusions: the "pick a path" option is gone (it used the Analyzer's storage browser). Package and segment exclusions remain.
- Dashboard tour no longer has the manual-tool (Swiper) step.
- `DashboardCardType` still lists the removed types so saved dashboard configs keep deserializing; they are never shown.

Follow-ups:

- Unit tests referencing removed code no longer compile (`DashboardMainActionEngineTest`, `DashboardViewModelResilienceTest`,
  `OneTapCleanerTest`, `LowSpaceMonitorTest`, `WidgetDataProviderTest`, `NavigationIdConsistencyTest`, `DashboardCardConfigTest`,
  and other Dashboard*Test files) plus `DashboardScreenshots.kt`. `app:assembleFossDebug` is unaffected; `testFossDebugUnitTest` is not.
- `crowdin.yml` still lists the removed modules' string files (harmless).

## Before sharing the APK

SD Maid SE is GPL-3.0, so a fork must stay GPL-3.0 with source available. The icons, logos and mascots are not
GPL-licensed: replace them and rename the app/application ID before distributing.

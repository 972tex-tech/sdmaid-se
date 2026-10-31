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

## Phase 2: delete, one tool per commit, CI after each

Order (least to most entangled): Swiper, Squeezer, Scheduler, Deduplicator, Analyzer.
Per tool:

1. Remove `include` from `settings.gradle`, `implementation`/`kover` lines from `app/build.gradle.kts` and root `build.gradle.kts`.
2. Delete the module directory.
3. Remove its imports/constructor params/flows in `DashboardViewModel`, `DashboardCardFlows`, `DashboardEvents*`, `DashboardScreen`, `ToolDashboardResources`.
4. Remove its branch from exhaustive `when`s (`submitTask`, `DashboardCardTypeExtensions`, `SerializationAppModule`).
5. Analyzer only: replace `AnalyzerSettings` use in `LowSpaceMonitor` and `WidgetDataProvider`.
6. Scheduler only: drop `SchedulerRestoreReceiver` from `AndroidManifest.xml` and `SchedulerTaskFactoryImpl`.
7. Delete matching tests; push; fix compile errors from the CI log.

## Before sharing the APK

SD Maid SE is GPL-3.0, so a fork must stay GPL-3.0 with source available. The icons, logos and mascots are not
GPL-licensed: replace them and rename the app/application ID before distributing.

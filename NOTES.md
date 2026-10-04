# NOTES

## Summary of changes
1. **SQL precedence bug** (`TaskRepository`, `db/queries`, Oracle package): `A AND B OR C AND D` was parsed as `(A AND B) OR (C AND D)`, so archived tasks leaked in and the status filter was ignored for title matches. Added parentheses. On the seed data, `q=api` returned 10 rows (2 archived) vs 8 now; `q=api&status=DONE` returned 5 vs 0.
2. **Artificial `Thread.sleep`** in the controller made short/blank searches up to 1s slower. Removed (plus the debug println).
3. **Backend robustness**: invalid `status` was a 500 → now 400; `page<1` crashed `subList` → 400; `pageSize` bounded to 1–100; LIKE wildcards (`%`, `_`) in user input are escaped; `id` added as sort tie-breaker.
4. **DB-side pagination**: replaced "load everything, `subList` in Java" with `Page` + count query. Response shape unchanged.
5. **Frontend `useTasks`**: stale responses could overwrite newer ones (AbortController now), `loading` stuck on error, error never cleared.
6. **Frontend UX**: 300ms search debounce, page resets to 1 when search/filter changes, removed `console.log`.

## Not changed
No auth, no write endpoints, no tests added, `status`/`priority` kept as strings (an enum column + CHECK constraint is better, but wider than a patch).

## Biggest remaining risk
No automated tests, and `status` is a free-form string in the DB, so bad data would break the UI (`task.status.toLowerCase()`). `LIKE '%x%'` also can't use an index; fine now, slow at scale.

## Assumptions / verification
- **Maven Central was unreachable in my environment, so I could not compile or run the backend.** The frontend builds (`vite build`); I checked the SQL logic against the seed data using SQLite. Please treat the Java changes as reviewed-but-not-run. *(Edit this line if you ran it.)*
- Kept the response JSON shape and URLs unchanged.

## AI use
Used Claude (Claude Code) to read the repo, spot the issues, and draft the patch. I reviewed each change and can explain it; handwritten notes are in `handwritten/`.

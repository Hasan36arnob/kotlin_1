# SplitWorth (Kotlin Android)

Sellable starter app in Kotlin + Jetpack Compose.

## Product
SplitWorth is a clean bill-splitting app with:
- Subtotal + tax + tip calculation
- Per-person split for groups
- Optional round-up mode
- Recent calculations list
- One-tap copy of result

## GitHub build (no Android Studio needed)

1. Create a new GitHub repo.
2. From this folder run:

```powershell
git init
git add .
git commit -m "Initial SplitWorth app"
git branch -M main
git remote add origin https://github.com/<your-user>/<your-repo>.git
git push -u origin main
```

3. Open your repo on GitHub.
4. Go to `Actions`.
5. Run `Android CI` or push code to `main`.
6. Download the APK from workflow `Artifacts`.

## Release APK and signing

For signed release builds, add these repository secrets:
- `SIGNING_KEYSTORE_BASE64`
- `SIGNING_STORE_PASSWORD`
- `SIGNING_KEY_ALIAS`
- `SIGNING_KEY_PASSWORD`

Then either:
- Run `Android Release` manually from `Actions`, or
- Push a version tag:

```powershell
git tag v1.0.0
git push origin v1.0.0
```

Tagged releases auto-publish APK files to GitHub Releases.

## What makes this valuable
- Real utility app with broad audience
- Cloud build pipeline ready for contractors/teams
- Base for monetization: ads, premium features, white-label builds

## Fast path to actual sale
1. Add AdMob banner/interstitial.
2. Add in-app purchase to remove ads and unlock export presets.
3. Publish on Play Store with strong screenshots and ASO.
4. Offer source-code license on marketplaces.

# GoRoutex Platform Core

Modular monolith workspace for Core Operations & Transaction Engine.

## Modules

- `platform-core-app`: Spring Boot entrypoint for the modular monolith.
- `platform-core-common`: shared cross-cutting code only.
- `platform-core-contracts`: protobuf and generated gRPC contracts.
- `platform-merchant`: merchant, route, trip, vehicle, and campaign context.
- `platform-booking`: booking, seat hold, and ticket context.
- `platform-driver`: driver profile, manifest, and passenger operation context.
- `platform-management`: admin and management context.
- `platform-payment`: payment orchestration and provider callback context.

## Migration Rules

- Keep existing REST endpoint paths unchanged.
- Preserve Clean/Hexagonal layers inside every feature module.
- Move code module by module and compile after each module.
- Replace internal network calls with local ports only after the migrated app compiles.

# 🎙️ Facilitator Guide: Multiplayer Banking Kata Workshop

This guide contains everything you need to host and facilitate a successful **90–120 minute unconference session**.

---

## 📅 Workshop Agenda & Timing

| Time | Phase | Facilitator Actions & Milestones |
| :--- | :--- | :--- |
| **00:00 - 00:15** | **Kickoff & Setup** | 1. Project the scoreboard on the main room screen (`http://localhost:9000`).<br>2. Explain the goal: Solo/Pair TDD implementation of a bank node that joins a live inter-bank network.<br>3. Review the 9 Object Calisthenics constraints.<br>4. Assist attendees with cloning, running tests, and localtunnel registration. |
| **00:15 - 00:45** | **Round 1: Core Domain (TDD)** | 1. Walk the room and encourage pair programming.<br>2. Remind participants to practice strict Outside-In TDD (Red-Green-Refactor).<br>3. Check for Calisthenics violations (e.g. primitives without value classes, getters exposing internal collections). |
| **00:45 - 01:15** | **Round 2: The SWIFT Network** | 1. Announce that the inter-bank network is active.<br>2. Have participants implement `POST /api/transfer-in` on their nodes.<br>3. Encourage teams to manually send test transfers to neighboring pairs via the scoreboard! |
| **01:15 - 01:40** | **Round 3: Chaos & Volume** | 1. Click **"Start Traffic Sim"** on the facilitator control panel.<br>2. Gradually increase volume or click **"Fire 20 Tx Surge"** to stress-test participant banks.<br>3. Point out live error rates, latency rankings, and leaderboard movements on the screen. |
| **01:40 - 01:50** | **Winner & Awards** | Announce rankings across categories:<br>- 🥇 **Most Resilient Bank** (Highest score & SLA compliance)<br>- 💰 **Richest Bank** (Largest settled balance)<br>- 💎 **Cleanest Calisthenics Design** (Elected by room vote) |
| **01:50 - 02:00** | **Retrospective** | Lead discussion on distributed transaction trade-offs, TDD velocity, and Object Calisthenics insights. |

---

## 🛠️ Facilitator Technical Setup (5 Minutes Before Session)

### 1. Launch the SWIFT Hub Server
```bash
./gradlew :swift-hub:run
```
By default, the server binds to port `9000`.

### 2. Open the Projector Dashboard
Open your browser to:
```text
http://localhost:9000
```
Connect your laptop to the projector or shared conference stream.

### 3. Share Your Local IP / Tunnel URL
Ensure attendees know your machine's IP address on the local WiFi network (or expose the SWIFT Hub via `npx localtunnel --port 9000` so attendees can reach it over HTTPS):
```bash
npx localtunnel --port 9000
```
Attendees will set `SWIFT_HUB_URL=https://<your-swift-hub-tunnel>.loca.lt` when launching their banks.

---

## 🎮 Facilitator Control Panel Cheatsheet

On the web scoreboard at `http://localhost:9000`:
- **▶ Start Traffic Sim**: Spawns continuous automated customer traffic between registered banks every 2 seconds.
- **⏹ Stop Sim**: Pauses traffic generation.
- **⚡ Fire 5 Tx Burst**: Sends a sudden small wave of 5 transactions.
- **💥 Fire 20 Tx Surge**: Sends a high-frequency spike of 20 rapid transfers across all banks.

---

## 🚑 Troubleshooting Network & Firewall Issues

- **Localtunnel Notification Page**: If attendees see a 403 or warning page from Localtunnel, ensure they include the header `Bypass-Tunnel-Reminder: true` or have opened the URL once in their browser.
- **Ngrok Fallback**: If Localtunnel has intermittent latency on conference WiFi, suggest using Ngrok: `ngrok http 8080`.
- **Direct LAN IP**: If everyone is on the same open WiFi network, participants can register with direct LAN IPs (e.g. `http://192.168.1.42:8080`) without needing any external tunnels.

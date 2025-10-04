# Presto Ride Sharing App - FSP Model

This directory contains the corrected FSP model for the Presto ride sharing app match making protocol.

## Files Description

- `presto_corrected.fsp` - The corrected FSP model that should work with LTSA analyzer
- `presto_model.fsp` - Previous version with syntax errors (for reference)
- `test_model.fsp` - Simple test model for syntax validation

## Key Corrections Made

### 1. Fixed Range and Parameter Syntax
- Corrected the range definitions: `range RIDERS = 1..N_RIDERS`
- Fixed process parameter syntax: `RIDER(ID)` instead of `RIDER(ID=RIDERS)`
- Removed invalid set comprehension syntax

### 2. Simplified Process Definitions
- Expanded all range references to explicit values (1, 2)
- Fixed conditional expressions using `when` statements
- Corrected process composition syntax

### 3. Fixed Property Definitions
- Corrected property syntax for FCFS verification
- Added proper ERROR states for violation detection
- Fixed progress property syntax for liveness verification

## Model Structure

### Processes
1. **RIDER(ID)** - Makes ride requests and waits for matching
2. **DRIVER(D)** - Waits for matches, completes rides, signals completion
3. **SCHEDULER(d_avail)** - Manages FCFS queue and driver availability
   - `Q_EMPTY` - No pending requests
   - `Q_ONE` - One pending request
   - `Q_TWO` - Two pending requests

### Properties Verified
1. **FCFS_12** - First-come, first-served for rider 1 then 2
2. **FCFS_21** - First-come, first-served for rider 2 then 1
3. **Rider1Served** - Liveness: rider 1 eventually gets served
4. **Rider2Served** - Liveness: rider 2 eventually gets served
5. **NoDeadlock** - Safety: system never deadlocks

## How to Use with LTSA

1. Download LTSA from: https://www.doc.ic.ac.uk/ltsa/
2. Load `presto_corrected.fsp` in LTSA
3. Compile the model
4. Check properties:
   - Right-click on `FCFS_12` and select "Check"
   - Right-click on `FCFS_21` and select "Check"
   - Right-click on `Rider1Served` and select "Check Progress"
   - Right-click on `Rider2Served` and select "Check Progress"
   - Right-click on `NoDeadlock` and select "Check"

## Expected Results

- **FCFS_12**: Should pass (no violations found)
- **FCFS_21**: Should pass (no violations found)
- **Rider1Served**: Should pass (progress satisfied)
- **Rider2Served**: Should pass (progress satisfied)
- **NoDeadlock**: Should pass (no deadlock states)

## Model Features

- **FCFS Enforcement**: The scheduler ensures requests are processed in order
- **Driver Availability Tracking**: Tracks number of available drivers
- **Queue Management**: Handles up to 2 pending requests
- **Completeness**: All requests eventually get served
- **Safety**: No deadlock states

## Key Design Decisions

1. **Abstraction Level**: Focused on match making protocol, abstracted away geographical details
2. **State Management**: Used parameterized processes to track driver availability
3. **Queue Implementation**: Explicit queue states (EMPTY, ONE, TWO) for clarity
4. **Property Verification**: Separate properties for different FCFS scenarios
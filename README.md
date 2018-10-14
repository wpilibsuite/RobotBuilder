# Robot Builder Project
[![Build Status](https://dev.azure.com/wpilib/DesktopTools/_apis/build/status/wpilibsuite.RobotBuilder)](https://dev.azure.com/wpilib/DesktopTools/_build/latest?definitionId=13)
[![codecov.io](http://codecov.io/github/wpilibsuite/RobotBuilder/coverage.svg?branch=master)](http://codecov.io/github/wpilibsuite/RobotBuilder?branch=master)

Welcome to the WPILib project. This repository contains the Robot Builder project. This program can be used to automatically generate Java and C++ FRC programs.

- [WPILib Mission](#wpilib-mission)
- [Building Robot Builder](#building-wpilib)
    - [Requirements](#requirements)
    - [Running Robot Builder](#running)
- [Contributing to WPILib](#contributing-to-wpilib)

## WPILib Mission

The WPILib Mission is to enable FIRST teams to focus on writing game-specific software rather than on hardware details - "raise the floor, don't lower the ceiling". We try to enable teams with limited programming knowledge and/or mentor experience to do as much as possible, while not hampering the abilities of teams with more advanced programming capabilities. We support Kit of Parts control system components directly in the library. We also strive to keep parity between major features of each language (Java, C++, and NI's LabVIEW), so that teams aren't at a disadvantage for choosing a specific programming language. WPILib is an open-source project, licensed under the BSD 3-clause license. You can find a copy of the license [here](BSD_License_for_WPILib_code.txt).

# Building Robot Builder

Building Robot Builder is very straightforward. WPILib uses Gradle to compile.

## Requirements
- [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## Running

To run robotbuilder use the command `./gradlew run`.

## Building

To build robotbuilder use the command `./gradlew shadowjar`. The runnable jar is `build\libs\RobotBuilder.jar`.

# Contributing to WPILib

See [CONTRIBUTING.md](CONTRIBUTING.md).

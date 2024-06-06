# RobotBuilder Project
[![CI](https://github.com/wpilibsuite/RobotBuilder/workflows/CI/badge.svg)

Welcome to the WPILib project. This repository contains the RobotBuilder project. This program can be used to automatically generate Java and C++ FRC programs using the [New Command Framework](https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html).

- [WPILib Mission](#wpilib-mission)
- [Building RobotBuilder](#building-robotbuilder)
    - [Requirements](#requirements)
    - [Running RobotBuilder](#running)
    - [Building RobotBuilder](#building)
- [Contributing to WPILib](#contributing-to-wpilib)

## WPILib Mission

The WPILib Mission is to enable FIRST teams to focus on writing game-specific software rather than on hardware details - "raise the floor, don't lower the ceiling". We try to enable teams with limited programming knowledge and/or mentor experience to do as much as possible, while not hampering the abilities of teams with more advanced programming capabilities. We support Kit of Parts control system components directly in the library. We also strive to keep parity between major features of each language (Java, C++, and NI's LabVIEW), so that teams aren't at a disadvantage for choosing a specific programming language. WPILib is an open-source project, licensed under the BSD 3-clause license. You can find a copy of the license [here](LICENSE.md).

# Building RobotBuilder

Building RobotBuilder is very straightforward. WPILib uses Gradle to compile.

## Requirements
- [JDK 17](https://adoptium.net/temurin/releases/?variant=openjdk17&jvmVariant=hotspot)
- A c++ Compiler (for c++ compile test). Any one of:
    - On Linux, install GCC 11+
    - On Windows, install [Visual Studio Community 2022](https://visualstudio.microsoft.com/vs/community/) and select the C++ programming language during installation
    - On macOS, install the Xcode command-line build tools via `xcode-select --install`
    - ARM compiler toolchain
        - Run `./gradlew installRoboRioToolchain` from a robot project
        - If the WPILib installer was used, this toolchain is already installed

## Running

To run RobotBuilder use the command `./gradlew run`.

## Building

To build RobotBuilder use the command `./gradlew shadowjar`. The runnable jar is `build\libs\RobotBuilder-all.jar`.

# Contributing to WPILib

See [CONTRIBUTING.md](CONTRIBUTING.md).

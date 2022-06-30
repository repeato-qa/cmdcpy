# cmdcpy (v0.1)

This is a stripped down fork of [ws-scrcpy](https://github.com/NetrisTV/ws-scrcpy/). It allows you to control Android devices remotely, but in comparison to scrcpy it does not support video streaming.

It does not require any _root_ access.

## Why no video streaming?

This project is used for controlling remote devices via [Repeato](https://www.repeato.app), a test automation tool for mobile Android and iOS Apps.
Repeato uses a different kind of device video streaming implementation and so we didn't have a need for that. 

## How to build?

See [BUILD].


## Developers

Read the [developers page].

[developers page]: DEVELOP.md


## Licence

    Copyright (C) 2018 Genymobile
    Copyright (C) 2018-2021 Romain Vimont

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Articles

- [Introducing scrcpy][article-intro]
- [Scrcpy now works wirelessly][article-tcpip]

[article-intro]: https://blog.rom1v.com/2018/03/introducing-scrcpy/
[article-tcpip]: https://www.genymotion.com/blog/open-source-project-scrcpy-now-works-wirelessly/


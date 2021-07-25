```
implementation "io.github.sckm:item-group:1.1.3"
```

# ItemGroup
`ItemGroup` is `Group` for [Groupie](https://github.com/lisawray/groupie)

`ItemGroup` is like `Section` and optimize performance for `Group` that contains only `Item`.  
Some methods(getItem, update,...) cost is lower than `Section`'s one.

## Example
Below are some average run times for reference:(Tests are run on Android Emulator with API level 29)

\ | N=100 | N=200 | N=300
:--:|:--:|:--:|:--:
Section | 23ms | 164ms | 2291ms
ItemGroup | 1ms | 4ms | 19ms


ItemGroup | Section
:--:|:--:
<img src="images/d7t86-q5z4i.gif" width="270" />|<img src="images/nxhff-s58xv.gif" width="270"/>


## License
```
Copyright 2019 scache

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

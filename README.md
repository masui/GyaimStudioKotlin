# Android版Gyaim

* Kotlinで書き直してみたけれど遅くなった気がする...
   *  検索のRegExpあたりが怪しい
* 最初に起動したとき落ちるし
* build.gradleに以下の記述が必要

   * testCompile 'org.json:json:20140107'
   * https://stackoverflow.com/questions/29402155/android-unit-test-not-mocked
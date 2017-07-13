# Android版Gyaim

* Kotlinで書き直してみたけれど遅くなった気がする...
   *  検索のRegExpあたりが怪しい
* 最初に起動したとき落ちるし


### 覚え

* テストでJSONを扱うとき、何故かbuild.gradleに以下の記述が必要

   * testCompile 'org.json:json:20140107'
   * 出典: https://stackoverflow.com/questions/29402155/android-unit-test-not-mocked

   
### わからないところ

* シングルトンobjectの初期化方法
* 署名したりAndroidManifestのバージョン番号を変えるには?
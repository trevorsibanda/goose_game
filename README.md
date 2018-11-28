# goose_game

### Steps to run

```bash
sbt compile
sbt run
```

## Example output

```bash
trevor@base2theory:~/workspace/my-own-x/goose_game$ sbt
compile
[info] Loading project definition from /home/trevor/workspace/my-own-x/goose_game/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to goose_game (in build file:/home/trevor/workspace/my-own-x/goose_game/)
[info] sbt server started at local:///home/trevor/.sbt/1.0/server/138c81359baa63559799/sock
sbt:goose_game> compile
[info] Compiling 3 Scala sources to /home/trevor/workspace/my-own-x/goose_game/target/scala-2.12/classes ...
[info] Done compiling.
[success] Total time: 4 s, completed Nov 27, 2018 10:00:28 PM
sbt:goose_game> run
[info] Packaging /home/trevor/workspace/my-own-x/goose_game/target/scala-2.12/goose_game_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running goose_game.Game 
add player pipo
Players: pipo
move pipo 1,6
pipo rolls 1,6 , pipo moves from  Start to 7
move pipo 1,2
pipo rolls 1,2 , pipo moves from 7 to 10
move pipo 2,2
pipo rolls 2,2 , pipo moves from 10 to 14, The Goose. pipo moves again and goes to 14, The Goose. pipo moves again and goes to 18
move pipo 12,2
Invalid command entered
move pipo
pipo rolls 4,4 , pipo moves from 18 to 26
move pipo 0,0
Invalid command entered
mmove pipo 5,5
Invalid command entered
move pipo 5,5
pipo rolls 5,5 , pipo moves from 26 to 36

```

``Specification``

See Goose.md

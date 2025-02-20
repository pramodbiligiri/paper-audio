Generate audio files from Arxiv paper metadata
==============================================

Contains three important top-level programs: fetch arxiv data, generate paper info from it, 
and convert the paper info to audio.
  
All information is stored in a Postgres database (see src/main/resources/schema.sql).

How to run on developer machine:  
```
$ mvn spring-boot:run -Dspring.profiles.active=dev -Dstart-class=audiogen.main.FetchArxivData  
$ mvn spring-boot:run -Dspring.profiles.active=dev -Dstart-class=audiogen.main.GeneratePaperInfo  
$ mvn spring-boot:run -Dspring.profiles.active=dev -Dstart-class=audiogen.main.GeneratePaperAudio  -Dspring-boot.run.arguments=5
```

There is a helper program `DumpAudioBytes`, which creates MP3 files from the generated audio. This can be used to
manually verify the results  
$ mvn spring-boot:run -Dspring.profiles.active=dev -Dstart-class=audiogen.main.DumpAudioBytes  

### Architecture
A Java Spring Boot application that uses Postgres for the backend. Uses standard Hibernate ORM.

For tests, uses a Postgres container via a 3rd party Maven dependency.
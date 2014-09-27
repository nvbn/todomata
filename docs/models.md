## Presentation model

Stored in elastic?

* owner-id
* task-id
* description
* depending -- task-ids
* created
* updated
* location -- lat-lon? text? -- first time location
* from -- date time
* to -- date time
* done
* deleted

## Changes model

Stored in mongo?

* type -- :create or :update
* created -- when :create -> == presentation.created, when :update -> == presentation.updated
* task-id -- only when :create
* data -- other presentation fields

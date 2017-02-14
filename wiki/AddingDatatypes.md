To add a data type to LibreTasks, first create a class for the new data type by subclassing libretasks.app.controller.datatypes.DataType. Next you need to register the new data type in the db. Finally you need to create a UI for entering data of this type. Now all that is left for you to do is to use the new data type in an event or action.

This does not yet introduce the capability to filter by the new data type, which is described below.

Introducing a data type usually happens in the context of a larger change, typically introducing an event or action, thus commits that introduce new data types usually have some further changes mixed in. Look at 86c475a and 854879e, for examples, but do note that the code has undergone some refactoring since. A more recent commit is 872723c, which introduces a new event as well as a data type with a filter and UI.

These are the files to change/add and their purpose:

A LibreTasks/src/libretasks/app/controller/datatypes/OmniSomeDataType.java -- the new data type.

M LibreTasks/src/libretasks/app/model/db/DbMigration.java -- register type in db.

M LibreTasks/src/libretasks/app/model/db/DbHelper.java -- increase db version by one.

## Add Filter ##

To add filters to a data type that does not support filters yet, follow the steps described below. If there is already a UI for the data type, or you’re adding another filter to a datatype that already has a filter, some of these things are already in place.

Provide a new view item class to edit data of the data type you would like to filter on.

In ViewItemFactory.java:

* Add a field for the data type ID.

* In `create()`, add code to call the new view item for your desired data type.

In your data type class:

* Create an enumeration named `Filter` as an inner class which implements `DataType.Filter`, and create at least one instance.

* Override `getFilterFromString()`.

* Provide an implementation for `matchFilter()` which returns true or false if called with your data type and a valid filter for it.

In DbMigration.java, register your new filters in the database like this:

```
DataFilterDbAdapter dataFilterDbAdapter = new DataFilterDbAdapter(db);
dataFilterDbAdapter.insert(OmniSomeDataType.Filter.SOME_FILTER.toString(),
	OmniSomeDataType.Filter.SOME_FILTER.displayName, dataTypeIdSomeType, dataTypeIdSomeType);
```

In RuleFilterViewFactory.java:

* Add a new member to the `AllFilterID` inner class for each filter:

```
public static final long SOMETYPE_SOMEFILTER = UIDbHelperStore.instance().getFilterLookup()
    .getDataFilterID(OmniSomeDataType.DB_NAME, OmniSomeDataType.Filter.SOME_FILTER.toString());

```

* In `buildUIForFilter()`, add code to build the UI for each of your new filters. Be sure to select the correct UI type for your data type.

A summary of the files to change and their purpose:

A LibreTasks/src/libretasks/app/view/simple/viewitem/SomeViewItem.java -- the UI for editing data of the newly introduced type.

M LibreTasks/src/libretasks/app/view/simple/viewitem/ViewItemFactory.java -- register the new UI.

M LibreTasks/src/libretasks/app/controller/datatypes/OmniSomeDataType.java -- add filter definitions for the data type.

M LibreTasks/src/libretasks/app/model/db/DbMigration.java -- register filters in db.

M LibreTasks/src/libretasks/app/model/db/DbHelper.java -- increase db version by one.


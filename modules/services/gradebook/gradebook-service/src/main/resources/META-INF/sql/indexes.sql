create index IX_3F9D4D9E on Gradebook_Assignment (groupId, status);
create index IX_E6C4F318 on Gradebook_Assignment (status);
create index IX_51DB1FA6 on Gradebook_Assignment (uuid_[$COLUMN_LENGTH:75$], companyId);
create unique index IX_F2D357A8 on Gradebook_Assignment (uuid_[$COLUMN_LENGTH:75$], groupId);
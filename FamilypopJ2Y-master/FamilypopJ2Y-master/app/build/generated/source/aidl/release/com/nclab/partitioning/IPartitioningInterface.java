/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\jsim\\AndroidStudioProjects\\FamilyPop\\app\\src\\main\\aidl\\com\\nclab\\partitioning\\IPartitioningInterface.aidl
 */
package com.nclab.partitioning;
public interface IPartitioningInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.nclab.partitioning.IPartitioningInterface
{
private static final java.lang.String DESCRIPTOR = "com.nclab.partitioning.IPartitioningInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.nclab.partitioning.IPartitioningInterface interface,
 * generating a proxy if needed.
 */
public static com.nclab.partitioning.IPartitioningInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.nclab.partitioning.IPartitioningInterface))) {
return ((com.nclab.partitioning.IPartitioningInterface)iin);
}
return new com.nclab.partitioning.IPartitioningInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerQuery:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.registerQuery(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_deregisterQuery:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.deregisterQuery(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateTaskType:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.updateTaskType(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startLogging:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.startLogging(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopLogging:
{
data.enforceInterface(DESCRIPTOR);
this.stopLogging();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.nclab.partitioning.IPartitioningInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int registerQuery(java.lang.String query) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(query);
mRemote.transact(Stub.TRANSACTION_registerQuery, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int deregisterQuery(int queryId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(queryId);
mRemote.transact(Stub.TRANSACTION_deregisterQuery, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public int updateTaskType(java.lang.String taskType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(taskType);
mRemote.transact(Stub.TRANSACTION_updateTaskType, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startLogging(java.lang.String filename) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(filename);
mRemote.transact(Stub.TRANSACTION_startLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopLogging() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopLogging, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerQuery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_deregisterQuery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_updateTaskType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_stopLogging = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int registerQuery(java.lang.String query) throws android.os.RemoteException;
public int deregisterQuery(int queryId) throws android.os.RemoteException;
public int updateTaskType(java.lang.String taskType) throws android.os.RemoteException;
public void startLogging(java.lang.String filename) throws android.os.RemoteException;
public void stopLogging() throws android.os.RemoteException;
}

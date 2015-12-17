/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\jsim\\AndroidStudioProjects\\FamilyPop\\app\\src\\main\\aidl\\com\\IFamilypopService\\IFamilypopService.aidl
 */
package com.IFamilypopService;
public interface IFamilypopService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.IFamilypopService.IFamilypopService
{
private static final java.lang.String DESCRIPTOR = "com.IFamilypopService.IFamilypopService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.IFamilypopService.IFamilypopService interface,
 * generating a proxy if needed.
 */
public static com.IFamilypopService.IFamilypopService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.IFamilypopService.IFamilypopService))) {
return ((com.IFamilypopService.IFamilypopService)iin);
}
return new com.IFamilypopService.IFamilypopService.Stub.Proxy(obj);
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
case TRANSACTION_DataToFamilypopService:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.DataToFamilypopService(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startServer:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.startServer();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopServer:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.stopServer();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_showClientsList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.showClientsList();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_initDevicesLocation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
boolean _result = this.initDevicesLocation(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getDevicesLocation:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
int _arg0_length = data.readInt();
if ((_arg0_length<0)) {
_arg0 = null;
}
else {
_arg0 = new int[_arg0_length];
}
int[] _arg1;
int _arg1_length = data.readInt();
if ((_arg1_length<0)) {
_arg1 = null;
}
else {
_arg1 = new int[_arg1_length];
}
int[] _arg2;
int _arg2_length = data.readInt();
if ((_arg2_length<0)) {
_arg2 = null;
}
else {
_arg2 = new int[_arg2_length];
}
double[] _arg3;
int _arg3_length = data.readInt();
if ((_arg3_length<0)) {
_arg3 = null;
}
else {
_arg3 = new double[_arg3_length];
}
boolean _result = this.getDevicesLocation(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
reply.writeIntArray(_arg0);
reply.writeIntArray(_arg1);
reply.writeIntArray(_arg2);
reply.writeDoubleArray(_arg3);
return true;
}
case TRANSACTION_startConversation:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.startConversation();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_stopConversation:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.stopConversation();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_connectToServer:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.connectToServer(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_disconnectFromServer:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.disconnectFromServer();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.IFamilypopService.IFamilypopService
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
@Override public void DataToFamilypopService(int a) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(a);
mRemote.transact(Stub.TRANSACTION_DataToFamilypopService, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**	Server-side API	*/
@Override public boolean startServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startServer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean stopServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopServer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String[] showClientsList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_showClientsList, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean initDevicesLocation(int x, int y) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(x);
_data.writeInt(y);
mRemote.transact(Stub.TRANSACTION_initDevicesLocation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean getDevicesLocation(int[] ids, int[] x, int[] y, double[] angles) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((ids==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(ids.length);
}
if ((x==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(x.length);
}
if ((y==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(y.length);
}
if ((angles==null)) {
_data.writeInt(-1);
}
else {
_data.writeInt(angles.length);
}
mRemote.transact(Stub.TRANSACTION_getDevicesLocation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
_reply.readIntArray(ids);
_reply.readIntArray(x);
_reply.readIntArray(y);
_reply.readDoubleArray(angles);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean startConversation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startConversation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean stopConversation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopConversation, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** Client-side API */
@Override public boolean connectToServer(java.lang.String ip) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(ip);
mRemote.transact(Stub.TRANSACTION_connectToServer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean disconnectFromServer() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnectFromServer, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_DataToFamilypopService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_showClientsList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_initDevicesLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getDevicesLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_startConversation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_stopConversation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_connectToServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_disconnectFromServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public void DataToFamilypopService(int a) throws android.os.RemoteException;
/**	Server-side API	*/
public boolean startServer() throws android.os.RemoteException;
public boolean stopServer() throws android.os.RemoteException;
public java.lang.String[] showClientsList() throws android.os.RemoteException;
public boolean initDevicesLocation(int x, int y) throws android.os.RemoteException;
public boolean getDevicesLocation(int[] ids, int[] x, int[] y, double[] angles) throws android.os.RemoteException;
public boolean startConversation() throws android.os.RemoteException;
public boolean stopConversation() throws android.os.RemoteException;
/** Client-side API */
public boolean connectToServer(java.lang.String ip) throws android.os.RemoteException;
public boolean disconnectFromServer() throws android.os.RemoteException;
}

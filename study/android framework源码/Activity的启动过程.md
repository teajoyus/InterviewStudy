博客：https://blog.csdn.net/gaugamela/article/details/53183216


https://blog.csdn.net/user11223344abc/article/details/81020118

Activity:

startActivity(Intent intent)

startActivity(Intent intent, @Nullable Bundle options)

startActivityForResult(@RequiresPermission Intent intent, int requestCode,@Nullable Bundle options)


Instrumentation：

 execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options)

 contextThread是ApplicationThread


在Instrumentation执行完后会执行下checkStartActivityResult
只要是activity启动有错误，都会在这个方法进行check后抛出异常

 ActivityManagerService：
 com.android.server.am.ActivityManagerService

 \frameworks\base\services\core\java\com\android\server\ActivityManagerService.java



 startActivity(IApplicationThread caller, String callingPackage,
             Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
             int startFlags, ProfilerInfo profilerInfo, Bundle bOptions)

   加多传了个：UserHandle.getCallingUserId()

 startActivityAsUser(IApplicationThread caller, String callingPackage,
             Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
             int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId)


ActivityStarter
/frameworks/base/services/core/java/com/android/server/am/ActivityStarter.java


startActivityMayWait(IApplicationThread caller, int callingUid,
            String callingPackage, Intent intent, String resolvedType,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode, int startFlags,
            ProfilerInfo profilerInfo, IActivityManager.WaitResult outResult, Configuration config,
            Bundle bOptions, boolean ignoreTargetSecurity, int userId,
            IActivityContainer iContainer, TaskRecord inTask)


startActivityLocked(IApplicationThread caller, Intent intent, Intent ephemeralIntent,
            String resolvedType, ActivityInfo aInfo, ResolveInfo rInfo,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            IBinder resultTo, String resultWho, int requestCode, int callingPid, int callingUid,
            String callingPackage, int realCallingPid, int realCallingUid, int startFlags,
            ActivityOptions options, boolean ignoreTargetSecurity, boolean componentSpecified,
            ActivityRecord[] outActivity, ActivityStackSupervisor.ActivityContainer container,
            TaskRecord inTask)

 startActivityUnchecked(final ActivityRecord r, ActivityRecord sourceRecord,
            IVoiceInteractionSession voiceSession, IVoiceInteractor voiceInteractor,
            int startFlags, boolean doResume, ActivityOptions options, TaskRecord inTask)
  低版本可能是叫startActivityUncheckedLoecked

  各种mLaunchFlags比如FLAG_ACTIVITY_NEW_TASK、FLAG_ACTIVITY_CLEAR_TASK 会在这里判断


ActivityStackSupervisor：

resumeFocusedStackTopActivityLocked()

resumeFocusedStackTopActivityLocked(
            ActivityStack targetStack, ActivityRecord target, ActivityOptions targetOptions) {
        if (targetStack != null && isFocusedStack(targetStack))




  ActivityStack
  /frameworks/base/services/core/java/com/android/server/am/ActivityStack.java


resumeTopActivityUncheckedLocked(ActivityRecord prev, ActivityOptions options)


resumeTopActivityInnerLocked(ActivityRecord prev, ActivityOptions options)

ActivityStackSupervisor：

 startSpecificActivityLocked(ActivityRecord r,
            boolean andResume, boolean checkConfig)

 realStartActivityLocked(ActivityRecord r, ProcessRecord app,
             boolean andResume, boolean checkConfig)

 ActivityThread的内部类ApplicationThread：

 scheduleLaunchActivity(Intent intent, IBinder token, int ident,
                ActivityInfo info, Configuration curConfig, Configuration overrideConfig,
                CompatibilityInfo compatInfo, String referrer, IVoiceInteractor voiceInteractor,
                int procState, Bundle state, PersistableBundle persistentState,
                List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents,
                boolean notResumed, boolean isForward, ProfilerInfo profilerInfo)

sendMessage(H.LAUNCH_ACTIVITY, r);


 ActivityThread的内部类H 继承Handler

handlerMessage里面


  ActivityThread：
  handleLaunchActivity(ActivityClientRecord r, Intent customIntent, String reason)

完成activity的创建
  performLaunchActivity(ActivityClientRecord r, Intent customIntent)

  然后调用

 handleResumeActivity(IBinder token,
            boolean clearHide, boolean isForward, boolean reallyResume, int seq, String reason)




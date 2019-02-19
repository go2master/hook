package test;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hook2 implements IXposedHookLoadPackage {

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass",
				String.class, new XC_MethodHook() {
					@Override
					protected void afterHookedMethod(MethodHookParam param)
							throws Throwable {
						if (param.hasThrowable())
							return;
						String classname = (String) param.args[0];
						if (classname.contains("CB3Des")) {
							XposedBridge.log(classname);
							Class<?> clazz = (Class<?>) param.getResult();
							XposedHelpers.findAndHookMethod(clazz, "encrypt",
									byte[].class, String.class, String.class,
									String.class, new XC_MethodHook() {
										@Override
										protected void beforeHookedMethod(
												MethodHookParam param)
												throws Throwable {
											String data = new String(
													(byte[]) param.args[0],
													"UTF-8");
											XposedBridge.log("request = "
													+ data);
											data = HttpUtils.executePOST(data);
											XposedBridge.log("response = "
													+ data);
										}
									});
						} else if (classname.contains("xxx")) {
							XposedBridge.log(classname);
							Class<?> clazz = (Class<?>) param.getResult();

							XposedHelpers
									.findAndHookMethod(
											clazz,
											"encrypt",
											XposedHelpers
													.findClass(
															"java.security.interfaces.RSAKey",
															ClassLoader
																	.getSystemClassLoader()),
											byte[].class, new XC_MethodHook() {
												@Override
												protected void beforeHookedMethod(
														MethodHookParam param)
														throws Throwable {
													String s = new String(
															(byte[]) param.args[1],
															"UTF-8");
													XposedBridge.log("rsa = "
															+ s);
												}
											});
						}
					}
				});
	}
}

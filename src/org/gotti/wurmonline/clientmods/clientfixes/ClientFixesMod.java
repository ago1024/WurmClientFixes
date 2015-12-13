package org.gotti.wurmonline.clientmods.clientfixes;

import javax.management.RuntimeErrorException;

import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.NewArray;

public class ClientFixesMod implements WurmMod, PreInitable {

	@Override
	public void preInit() {

		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass ctWurmEventHandler = classPool.get("com.wurmonline.client.WurmEventHandler");
			CtConstructor ctHandleEvents = ctWurmEventHandler.getConstructor(
					Descriptor.ofConstructor(new CtClass[] { classPool.get("com.wurmonline.client.game.World"),
							classPool.get("com.wurmonline.client.LwjglClient") }));

			ctHandleEvents.instrument(new ExprEditor() {
				
				@Override
				public void edit(NewArray a) throws CannotCompileException {
					try {
						if (a.getComponentType().equals(CtClass.booleanType)) {
							StringBuffer code = new StringBuffer();
							code.append("if ($1 == 2) { $_ = $proceed(3); } else { $_ = $proceed($$); }");
							a.replace(code.toString());
						}
					} catch (NotFoundException e) {
						throw new HookException(e);
					}
				}
			});

		}
		catch (NotFoundException | CannotCompileException e) {
			throw new HookException(e);
		}

	}

}

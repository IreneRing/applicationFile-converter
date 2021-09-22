package com.plugins.appFileConverter;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.plugins.appFileConverter.consts.Constant;
import com.plugins.appFileConverter.consts.MsgConsts;
import com.plugins.appFileConverter.props2yaml.Props2Yaml;
import com.plugins.appFileConverter.utils.CommonUtils;
import com.plugins.appFileConverter.yaml2props.Yaml2Props;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author zxy
 */
public class NewYamlAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Presentation presentation = event.getPresentation();
        //根据类型动态控制Action的隐藏显示
        //prop类型
        event.getPresentation().setEnabledAndVisible(CommonUtils.containFileType(event, Constant.PROPERTIES) && presentation.isEnabled());

    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent event) {
        final VirtualFile file = CommonUtils.getVirtualFile(event);

        if (null == file) {
            Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.NO_FILE_SELECTED,
                    MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
            return;
        }
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String content = new String(file.contentsToByteArray());
                if (StringUtil.isEmpty(content)) {
                    Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.FILE_NOT_EMPTY,
                            MsgConsts.SELECT_FILE_FIRST, NotificationType.ERROR));
                    return;
                }
                //PROPERTIES文件处理
                //新建文件
                final VirtualFile virtualFile = CommonUtils.createVirtualFile(event, file.getNameWithoutExtension(), Constant.YAML_SUFFIX);

                String propsContent = Props2Yaml.fromContent(content).convert();
                virtualFile.setBinaryContent(propsContent.getBytes());
                Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID, MsgConsts.SUCCESS,
                        MsgConsts.PROPS2YAML, NotificationType.INFORMATION));

            } catch (IOException e) {
                Notifications.Bus.notify(new Notification(Constant.GROUP_DISPLAY_ID,
                        MsgConsts.CANNOT_RENAME_FILE, e.getMessage(), NotificationType.ERROR));
            }
        });
    }

}

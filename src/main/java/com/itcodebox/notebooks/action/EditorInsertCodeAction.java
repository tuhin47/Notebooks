package com.itcodebox.notebooks.action;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.popup.*;
import com.intellij.psi.PsiFile;
import com.intellij.ui.BalloonImpl;
import com.intellij.util.ui.JBUI;
import com.itcodebox.notebooks.constant.PluginColors;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;

/**
 * @author LeeWyatt
 */
public class EditorInsertCodeAction extends DumbAwareAction {

    /**
     * 根据不同的情况,来判断是否禁用以及是否显示添加笔记的菜单项
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = e.getData(CommonDataKeys.PROJECT);
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //设置可见性并仅在现有项目和编辑器以及存在选择的情况下启用
        e.getPresentation().setEnabledAndVisible(
                //!AppSettingsState.getInstance().readOnlyMode&&
                project != null
                        && editor != null
                        && editor.getDocument().isWritable()
                        && !editor.getSelectionModel().hasSelection()
                        && psiFile != null
        );
    }
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        InsertPanelFactory factory = new InsertPanelFactory();
        JPanel insertCodePanel = factory.createInsertPanel(project, editor);
        
        Balloon balloon = JBPopupFactory.getInstance()
                .createDialogBalloonBuilder(insertCodePanel, null)
                .setShadow(true)
                .setDialogMode(true)
                .setRequestFocus(true)
                .setHideOnAction(true)
                .setHideOnCloseClick(true)
                .setHideOnKeyOutside(false)
                .setHideOnFrameResize(true)
                .setHideOnClickOutside(true)
                .setBlockClicksThroughBalloon(true)
                .setCloseButtonEnabled(false)
                .setAnimationCycle(200)
                .setBorderColor(PluginColors.INSERT_BALLOON_BORDER)
                .setFillColor(JBUI.CurrentTheme.CustomFrameDecorations.paneBackground())
                .createBalloon();
        
        balloon.addListener(new JBPopupListener() {
            @Override
            public void onClosed(@NotNull LightweightWindowEvent event) {
                balloon.hide();
            }
        });
        
        factory.setBalloonImpl((BalloonImpl) balloon);
        balloon.show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.atRight);
    }
    
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread ()
    {
        return ActionUpdateThread.BGT;
    }
}

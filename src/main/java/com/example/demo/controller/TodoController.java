package com.example.demo.controller;

import com.example.demo.model.Todo;
import com.example.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("todos", todoService.findAll());
        return "todo/list";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "todo/form";
    }

    @PostMapping("/confirm")
    public String confirm(@RequestParam("title") String title, Model model) {
        String normalizedTitle = title == null ? "" : title.trim();
        if (normalizedTitle.isEmpty()) {
            model.addAttribute("error", "タイトルを入力してください");
            return "todo/form";
        }

        model.addAttribute("title", normalizedTitle);
        return "todo/confirm";
    }

    @PostMapping("/complete")
    public String complete(@RequestParam("title") String title, Model model, RedirectAttributes redirectAttributes) {
        String normalizedTitle = title == null ? "" : title.trim();
        if (normalizedTitle.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "タイトルを入力してください");
            return "redirect:/todo/new";
        }

        Todo todo = Todo.builder()
                .title(normalizedTitle)
                .completed(false)
                .build();
        todoService.create(todo);
        model.addAttribute("title", normalizedTitle);
        return "todo/complete";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return todoService.findById(id)
                .map(todo -> {
                    model.addAttribute("todo", todo);
                    return "todo/edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "対象のToDoが見つかりません");
                    return "redirect:/todo";
                });
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @RequestParam("title") String title,
                         RedirectAttributes redirectAttributes) {
        String normalizedTitle = title == null ? "" : title.trim();
        if (normalizedTitle.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "タイトルを入力してください");
            return "redirect:/todo/" + id + "/edit";
        }

        return todoService.findById(id)
                .map(todo -> {
                    todo.setTitle(normalizedTitle);
                    todoService.update(todo);
                    redirectAttributes.addFlashAttribute("success", "更新が完了しました");
                    return "redirect:/todo";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "対象のToDoが見つかりません");
                    return "redirect:/todo";
                });
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = todoService.deleteById(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "ToDoを削除しました");
            } else {
                redirectAttributes.addFlashAttribute("error", "削除に失敗しました");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "削除に失敗しました");
        }
        return "redirect:/todo";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean toggled = todoService.toggleCompleted(id);
        if (!toggled) {
            redirectAttributes.addFlashAttribute("error", "対象のToDoが見つかりません");
        }
        return "redirect:/todo";
    }
}

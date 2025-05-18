package com.example.dailysteps.domain.usecase.tasks

class DuplicateTaskException :
    Exception("Задача с таким описанием уже есть на этот день")
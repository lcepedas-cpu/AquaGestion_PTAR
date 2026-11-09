# 🌊 AquaGestion_PTAR

Simulador interactivo en Java (Swing / MVC) para la gestión y seguimiento de fases de Tratamiento de Aguas Residuales (PTAR), validando el cumplimiento ambiental según la **Resolución 0631 de 2015 de Colombia**.

---

## 🚀 Módulos del Sistema

El aplicativo consta de 5 ventanas interconectadas secuencialmente:

1. **Captación:** Ingreso de caudal base, tipo de fuente y estado operativo (cálculo de caudal real y SST inicial).
2. **Tratamiento Primario:** Remoción física de sólidos sedimentables y detección de sobrecapacidad.
3. **Reactor Biológico:** Control de aireación (Oxígeno Disuelto), remoción orgánica y alertas de anoxia.
4. **Clarificación y Desinfección:** Evaluación de métodos de desinfección (Cloro vs. UV) y balance de lodos.
5. **Evaluación Ambiental:** Semáforo normativo de vertimientos (Res. 0631 de 2015), métricas globales de eficiencia y exportación de reportes técnicos `.TXT`.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 17 / 21)
* **GUI:** Java Swing / NetBeans GUI Builder
* **Arquitectura:** Modelo-Vista-Controlador (MVC)
* **Gestor de dependencias:** Maven

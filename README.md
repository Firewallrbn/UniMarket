# UniMarket — Proyecto de Diseño de Software (Corte 1)

## 1) Presentación del Problema
En la Universidad de La Sabana existe una dinámica constante de compra y venta de productos y servicios entre estudiantes (emprendimientos, artículos de segunda mano, útiles, tecnología, etc.). Sin embargo, estas transacciones suelen estar dispersas en chats y grupos informales, lo que reduce la visibilidad de los emprendedores, dificulta encontrar ofertas específicas y genera fricción para concretar compras.

*UniMarket* propone un marketplace universitario que centraliza el catálogo, permite consultar productos por categorías y simula el flujo de compra/venta, con un diseño enfocado en buenas prácticas de arquitectura, SOLID y patrones de diseño.

*Beneficiarios principales:* estudiantes compradores, estudiantes emprendedores/vendedores y la comunidad universitaria en general.

---

## 2) Creatividad en la Presentación
 *Video creativo (problemática):* (pegar aquí el enlace al video)  
Breve descripción: el video presenta la problemática de la compra/venta informal dentro del campus y la necesidad de una plataforma centralizada para visibilizar emprendimientos y facilitar transacciones con una pequeña presentacion de lo que es UniMarket.

---

## 3) Fundamentos de Ingeniería de Software 

### 3.1 Mantenibilidad
El sistema está organizado por paquetes (UI, servicios, repositorios, estrategias, factories, modelos). Esta separación permite modificar una parte (por ejemplo, cambiar el repositorio CSV por otro almacenamiento) sin afectar la lógica de negocio.

### 3.2 Escalabilidad / Extensibilidad
El diseño se apoya en interfaces y patrones (Factory, Strategy), permitiendo agregar nuevas categorías de productos, nuevas reglas de comisión o nuevas fuentes de datos sin reescribir módulos existentes (principio OCP).

### 3.3 Bajo acoplamiento
Los servicios dependen de abstracciones (interfaces) en lugar de implementaciones concretas, reduciendo el acoplamiento entre capas y facilitando pruebas y cambios de infraestructura (principio DIP).

---

## 4) Diseño de Software

### 4.1 Principios SOLID aplicados 

#### SRP — Single Responsibility Principle
Separamos responsabilidades por componentes:
- *UI (Consola):* interacción con el usuario y navegación de menús.
- *Servicios:* reglas y orquestación de casos de uso (mercado, autenticación, ventas).
- *Repositorios:* persistencia/lectura de datos (CSV).
- *Estrategias:* cálculo de comisión.

Esto evita clases sobrecargadas y concentra los cambios donde corresponde.

#### OCP — Open/Closed Principle
El cálculo de comisión se implementa con Strategy, lo que permite crear nuevas reglas de comisión sin modificar el flujo principal del checkout:
- StandardCommissionStrategy
- EntrepreneurCommissionStrategy
- ScholarshipCommissionStrategy
(Agregar una nueva comisión = crear nueva estrategia.)

#### DIP — Dependency Inversion Principle
Los módulos de alto nivel (servicios) no dependen de repositorios concretos, sino de interfaces. Así, la lógica del sistema no queda “amarrada” a CSV y puede migrar a BD o memoria sin reescribir la capa de negocio.

---

### 4.2 Patrones de diseño utilizados 

#### Patrón 1 — Factory (Creacional)
Se utiliza una factory general y factories por categoría para crear productos según su familia:
- Factory general: IMarketFactory
- Factories concretas por categoría: TechFactory, SchoolSuppliesFactory 

*Problema que resuelve:* centraliza la creación de productos por familia y permite agregar nuevas categorías sin alterar el código existente del flujo principal.

#### Patrón 2 — Strategy (Comportamiento)
Se utiliza Strategy para calcular comisiones de forma intercambiable:
- Interfaz: ICommissionStrategy
- Contexto: CommissionContext
- Estrategias concretas: Standard, Entrepreneur, Scholarship

*Problema que resuelve:* evita condicionales extensos (if/else) en el checkout y permite extender reglas de comisión sin modificar el servicio de ventas.

---

### 4.3 Diagrama UML
https://mermaid.ai/d/465ac8c8-e6b4-4328-ac93-f2367a814e1b

---

### 4.4 Diagramas de casos de uso o secuencia (recomendado)
📌 *Caso de uso / Secuencia:* 
![f8b3603e-c192-4f59-9dcd-eccafee569ed](https://github.com/user-attachments/assets/2cf263f6-7100-4322-9dde-25c59db3738d)

---

## 5) Implementación

### 5.1 Estructura del proyecto (paquetes)
El proyecto está organizado siguiendo separación por responsabilidades:
- ui → interacción por consola
- auth → autenticación / sesión
- market → lógica del marketplace
- repository → lectura/escritura de datos en CSV
- model → entidades del dominio
- strategy → cálculo de comisiones (Strategy)
- factory → creación de productos por familia/categoría (Factory)

### 5.2 Persistencia
La persistencia se realiza mediante archivos CSV ubicados en la carpeta data/:
- data/users.csv
- data/products.csv
- data/sales.csv

---

## 6) Análisis Técnico (Cohesión, Acoplamiento y Calidad)

### Cohesión
Cada clase y paquete se centra en una responsabilidad específica (UI, servicios, repositorios, modelos, estrategias). Esto mejora legibilidad y reduce el impacto de cambios.

### Bajo acoplamiento
Los servicios se comunican con repositorios mediante interfaces, permitiendo reemplazar implementaciones sin tocar la lógica principal del sistema.

### Cumplimiento de atributos de calidad
- *Mantenibilidad:* estructura modular + separación de capas.
- *Escalabilidad:* Strategy y Factory facilitan extensión.
- *Bajo acoplamiento:* uso de interfaces y dependencia hacia abstracciones.

---

## 7) Cómo ejecutar el proyecto (Maven)

### Requisitos
- *Java 17*
- *Maven*

### Ejecución
1. Abrir una terminal en la carpeta donde está el archivo pom.xml.
2. Ejecutar:

```bash
mvn clean compile
mvn exec:java

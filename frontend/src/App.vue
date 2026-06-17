<template>
  <div class="app-shell" :class="{ dark: isDark }">
    <!-- ═══ HEADER BAR ═══ -->
    <header class="topbar" v-if="user">
      <div class="topbar-left">
        <button class="mobile-nav-btn" @click="sidebarOpen = !sidebarOpen" aria-label="打开导航">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><line x1="4" y1="7" x2="20" y2="7"/><line x1="4" y1="12" x2="16" y2="12"/><line x1="4" y1="17" x2="20" y2="17"/></svg>
        </button>
        <span class="logo-dot"></span>
        <span class="logo-text">ExamOnline</span>
      </div>
      <div class="topbar-right">
        <div class="notif-wrap">
          <button class="notif-btn" @click="showNotif = !showNotif" title="通知">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
            <span class="notif-dot" v-if="notifications.length"></span>
          </button>
          <div class="notif-panel" v-if="showNotif">
            <div class="notif-item" v-if="!notifications.length" style="color:var(--text-muted)">暂无通知</div>
            <div class="notif-item" v-for="(n,i) in notifications" :key="i" @click="notifications.splice(i,1)">
              <div>{{ n.text }}</div>
              <div class="notif-time">{{ n.time }}</div>
            </div>
          </div>
        </div>
        <button class="theme-toggle" @click="isDark = !isDark" :title="isDark ? '切换亮色' : '切换暗色'">
          <svg v-if="!isDark" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
          <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>
        </button>
        <button class="theme-toggle" @click="openProfile = true" title="个人中心">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        </button>
        <button class="btn ghost sm" @click="logout">退出</button>
      </div>
    </header>

    <!-- ═══ LOGIN ═══ -->
    <div v-if="!user" class="login-wrap">
      <div class="login-hero card">
        <div class="eyebrow">Spring Boot + Vue 3</div>
        <h1>在线考试系统</h1>
        <p>管理员、教师、学生三端联动，支持题库管理、自动组卷、防切屏考试、成绩分析与错题本。</p>
        <div class="feature-pills">
          <span class="badge">题库导入导出</span>
          <span class="badge">自动阅卷</span>
          <span class="badge">防切屏交卷</span>
          <span class="badge">成绩分析</span>
        </div>
      </div>
      <div class="card login-form">
        <div class="section-icon-wrap">
          <div class="section-icon">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          </div>
          <h3>登录系统</h3>
        </div>
        <div class="grid">
          <div class="form-group">
            <label class="form-label">用户名</label>
            <input class="input" v-model="loginForm.username" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label class="form-label">密码</label>
            <input class="input" v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="login" />
          </div>
          <button class="btn lg" @click="login" :disabled="loggingIn">
            <svg v-if="loggingIn" class="spin" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12a9 9 0 1 1-6.219-8.56"/></svg>
            {{ loggingIn ? '登录中…' : '进入系统' }}
          </button>
        </div>
        <div class="quick-login">
          <button class="chip" @click="fillLogin('admin','admin123')">管理员</button>
          <button class="chip" @click="fillLogin('teacher','teacher123')">教师</button>
          <button class="chip" @click="fillLogin('student','student123')">学生</button>
        </div>
        <p class="muted" style="margin-top:8px">默认账号：admin / teacher / student，密码同用户名+123</p>
        <div v-if="message" :class="messageType === 'error' ? 'err-msg' : 'notice'">{{ message }}</div>
      </div>
    </div>

    <template v-else>
      <div class="workspace-layout" :class="{ 'sidebar-open': sidebarOpen }">
        <div v-if="sidebarOpen" class="sidebar-scrim" @click="sidebarOpen = false"></div>
        <aside class="sidebar" aria-label="系统侧边栏">
          <div class="sidebar-panel">
            <div class="sidebar-brand">
              <span class="sidebar-mark"></span>
              <div>
                <strong>ExamOnline</strong>
                <span>智能在线考试平台</span>
              </div>
            </div>
            <div class="sidebar-user">
              <div class="user-avatar">{{ userInitials }}</div>
              <div>
                <strong>{{ user.realName || user.username }}</strong>
                <span>{{ roleLabel }} · {{ user.department || user.className || '未设置组织' }}</span>
              </div>
            </div>
            <nav class="side-nav">
              <button
                v-for="item in navItems"
                :key="item.key"
                class="side-link"
                :class="{ active: activeNavKey === item.key }"
                @click="goSection(item)"
              >
                <span class="side-icon">{{ item.code }}</span>
                <span class="side-copy">
                  <strong>{{ item.label }}</strong>
                  <small>{{ item.desc }}</small>
                </span>
                <span v-if="item.badge !== undefined" class="side-badge">{{ item.badge }}</span>
              </button>
            </nav>
            <button class="side-profile" @click="openProfile = true; sidebarOpen = false">
              <span>账号与安全</span>
              <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
            </button>
          </div>
        </aside>
        <main class="workspace-main">
      <!-- ═══ NAVIGATION ═══ -->
      <div class="dashboard-top">
        <div class="tabs role-tabs">
          <button v-if="isAdmin" class="tab" :class="{ active: tab === 'admin' }" @click="tab='admin'; activeNavKey='overview'; loadAdmin()">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>
            管理员端
          </button>
          <button v-if="isTeacher" class="tab" :class="{ active: tab === 'teacher' }" @click="tab='teacher'; activeNavKey='overview'; loadTeacher()">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
            教师端
          </button>
          <button v-if="isStudent" class="tab" :class="{ active: tab === 'student' }" @click="tab='student'; activeNavKey='overview'; loadStudent()">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c0 2 3 3 6 3s6-1 6-3v-5"/></svg>
            学生端
          </button>
        </div>
        <div class="search-bar">
          <div class="form-group" style="margin:0">
            <input v-model="globalSearch" class="input" placeholder="全局搜索题目、试卷、用户…" />
          </div>
        </div>
      </div>

      <section class="module-hero">
        <div>
          <span class="eyebrow">{{ roleLabel }} Workspace</span>
          <h1>{{ activeNavItem.label }}</h1>
          <p>{{ activeNavItem.desc }}</p>
        </div>
        <div class="module-actions">
          <button class="btn ghost sm" @click="loadAll">刷新当前数据</button>
          <button v-if="isTeacher" class="btn secondary sm" @click="goSection(navItems.find(item => item.key === 'paper-build'))">快速组卷</button>
          <button v-if="isStudent && student.upcoming.length" class="btn secondary sm" @click="goSection(navItems.find(item => item.key === 'upcoming'))">进入待考</button>
        </div>
      </section>

      <div v-if="globalSearchResults.length" class="global-results">
        <button v-for="item in globalSearchResults" :key="item.key" class="result-card" @click="openGlobalResult(item)">
          <span>{{ item.type }}</span>
          <strong>{{ item.title }}</strong>
          <small>{{ item.desc }}</small>
        </button>
      </div>

      <!-- ═══ STATS BAR ═══ -->
      <div v-if="activeNavKey === 'overview'" id="overviewPanel" class="stats-grid">
        <div class="stat-card" v-for="(card, i) in overviewCards" :key="i">
          <div class="stat-label">{{ card.label }}</div>
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-tip">{{ card.tip }}</div>
        </div>
      </div>

      <section v-if="activeNavKey === 'overview'" class="workspace-overview">
        <div class="feature-grid">
          <button v-for="item in workspaceFeatureCards" :key="item.key" class="feature-card" @click="goSection(item)">
            <span class="feature-code">{{ item.code }}</span>
            <div>
              <strong>{{ item.label }}</strong>
              <small>{{ item.desc }}</small>
            </div>
            <span v-if="item.badge !== undefined" class="feature-badge">{{ item.badge }}</span>
          </button>
        </div>
        <div class="signal-grid">
          <div v-for="item in workspaceSignals" :key="item.title" class="signal-card" :class="'tone-' + item.tone">
            <span>{{ item.label }}</span>
            <strong>{{ item.title }}</strong>
            <p>{{ item.desc }}</p>
          </div>
        </div>
      </section>


      <!-- ═══ ADMIN TAB ═══ -->
      <div v-if="tab==='admin' && isAdmin && activeNavKey !== 'overview'" class="grid module-page">
        <div v-if="activeNavKey === 'account-form' || activeNavKey === 'classes'" class="grid grid-3">
          <!-- User Form -->
          <div v-if="activeNavKey === 'account-form'" class="card panel-card" id="userFormCard">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                </div>
                账号管理
              </h3>
              <span class="badge blue">{{ admin.userForm.id ? '编辑账号' : '新增账号' }}</span>
            </div>
            <div class="identity-summary">
              <div>
                <span>当前粒度</span>
                <strong>角色 / 院系 / 专业 / 班级 / 编号 / 状态</strong>
              </div>
              <button class="btn ghost sm" @click="resetUserForm">清空表单</button>
            </div>
            <div class="grid identity-grid">
              <div class="form-group"><label class="form-label">用户名</label><input class="input" v-model="admin.userForm.username" placeholder="如 zhangsan" /></div>
              <div class="form-group"><label class="form-label">密码（编辑可留空）</label><input class="input" v-model="admin.userForm.password" placeholder="初始密码" /></div>
              <div class="form-group"><label class="form-label">姓名</label><input class="input" v-model="admin.userForm.realName" placeholder="真实姓名" /></div>
              <div class="form-group"><label class="form-label">角色</label><select class="select" v-model="admin.userForm.role"><option>ADMIN</option><option>TEACHER</option><option>STUDENT</option></select></div>
              <div class="form-group"><label class="form-label">学号</label><input class="input" v-model="admin.userForm.studentNo" placeholder="学生账号填写" /></div>
              <div class="form-group"><label class="form-label">工号</label><input class="input" v-model="admin.userForm.employeeNo" placeholder="教师/管理员填写" /></div>
              <div class="form-group"><label class="form-label">所属班级</label><input class="input" v-model="admin.userForm.className" placeholder="如 软件工程 1 班" list="classNames" /></div>
              <div class="form-group"><label class="form-label">院系</label><input class="input" v-model="admin.userForm.department" placeholder="如 计算机学院" /></div>
              <div class="form-group"><label class="form-label">专业</label><input class="input" v-model="admin.userForm.major" placeholder="如 软件工程" /></div>
              <div class="form-group"><label class="form-label">账号状态</label><select class="select" v-model="admin.userForm.enabled"><option :value="true">启用</option><option :value="false">停用</option></select></div>
              <datalist id="classNames">
                <option v-for="c in admin.classes" :key="c.id" :value="c.name"></option>
              </datalist>
              <div class="form-actions">
                <button class="btn" @click="saveUser">{{ admin.userForm.id ? '更新用户（#'+admin.userForm.id+'）' : '新增用户' }}</button>
                <button class="btn ghost" @click="resetUserForm">重置</button>
              </div>
            </div>
          </div>

          <div v-if="activeNavKey === 'classes'" class="card panel-card" id="classFormCard">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>
                </div>
                班级管理
              </h3>
              <span class="badge">组织架构</span>
            </div>
            <div class="grid">
              <div class="form-group"><label class="form-label">班级名称</label><input class="input" v-model="admin.classForm.name" placeholder="班级名称" /></div>
              <div class="form-group"><label class="form-label">院系</label><input class="input" v-model="admin.classForm.department" placeholder="院系" /></div>
              <div class="form-group"><label class="form-label">专业</label><input class="input" v-model="admin.classForm.major" placeholder="专业" /></div>
              <div class="form-group"><label class="form-label">学生规模</label><input class="input" v-model.number="admin.classForm.studentCount" type="number" min="0" placeholder="学生数" /></div>
              <button class="btn" @click="saveClass">{{ admin.classForm.id ? '更新班级（#'+admin.classForm.id+'）' : '新增班级' }}</button>
            </div>
          </div>

          <!-- Stats -->
          <div class="card panel-card">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="20" x2="18" y2="10"/><line x1="12" y1="20" x2="12" y2="4"/><line x1="6" y1="20" x2="6" y2="14"/></svg>
                </div>
                系统快照
              </h3>
              <span class="badge green">实时</span>
            </div>
            <template v-if="admin.stats">
              <div class="mini-stats">
                <div><span>用户</span><strong>{{ admin.stats.userCount }}</strong></div>
                <div><span>班级</span><strong>{{ admin.stats.classCount }}</strong></div>
                <div><span>题目</span><strong>{{ admin.stats.questionCount }}</strong></div>
                <div><span>试卷</span><strong>{{ admin.stats.publishedPaperCount }}</strong></div>
                <div><span>答卷</span><strong>{{ admin.stats.attemptCount }}</strong></div>
              </div>
              <div class="admin-segments" v-if="adminDepartmentSegments.length">
                <div v-for="item in adminDepartmentSegments" :key="item.name">
                  <span>{{ item.name }}</span><strong>{{ item.count }}</strong>
                </div>
              </div>
            </template>
            <div v-else-if="adminLoading" class="empty-state"><p>加载中…</p></div>
          </div>
        </div>

        <div v-if="activeNavKey === 'users' || activeNavKey === 'classes'" class="grid grid-2">
          <!-- Users -->
          <div v-if="activeNavKey === 'users'" class="card" id="usersPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>
                </div>
                用户列表
              </h3>
              <div class="row">
                <input v-model="adminUserFilter" class="input compact" placeholder="搜索用户…" />
                <select v-model="adminUserRoleFilter" class="select compact">
                  <option value="">全部角色</option>
                  <option value="ADMIN">管理员</option>
                  <option value="TEACHER">教师</option>
                  <option value="STUDENT">学生</option>
                </select>
                <select v-model="adminUserStatusFilter" class="select compact">
                  <option value="">全部状态</option>
                  <option value="enabled">启用</option>
                  <option value="disabled">停用</option>
                </select>
                <button class="btn ghost sm" @click="loadAdmin">刷新</button>
                <button class="btn ghost sm danger" :disabled="selectedUserIds.length === 0" @click="deleteSelectedUsers">
                  批量删除<span v-if="selectedUserIds.length">（{{ selectedUserIds.length }}）</span>
                </button>
              </div>
            </div>
            <div class="governance-strip">
              <button
                v-for="item in adminGovernanceSegments"
                :key="item.key"
                class="governance-card"
                :class="{ active: adminUserRoleFilter === item.role && adminUserStatusFilter === item.status }"
                @click="setAdminSegment(item)"
              >
                <span>{{ item.label }}</span>
                <strong>{{ item.count }}</strong>
                <small>{{ item.hint }}</small>
              </button>
            </div>
            <div class="table-wrap">
              <table class="table">
                <thead>
                  <tr>
                    <th>
                      <input type="checkbox" :checked="allUsersOnPageSelected" :disabled="paginatedUsers.length === 0" @change="toggleUserPageSelection($event.target.checked)" />
                    </th>
                    <th>账号</th><th>身份</th><th>组织</th><th>编号</th><th>状态</th><th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="u in paginatedUsers" :key="u.id">
                    <td>
                      <input type="checkbox" :checked="selectedUserIds.includes(u.id)" @change="toggleUserSelection(u.id)" />
                    </td>
                    <td><b>{{ u.username }}</b><div class="muted">{{ u.realName || '-' }}</div></td>
                    <td><span class="badge" :class="roleBadge(u.role)">{{ roleName(u.role) }}</span></td>
                    <td>{{ formatOrg(u) }}</td>
                    <td>{{ u.studentNo || u.employeeNo || '-' }}</td>
                    <td><span class="badge" :class="u.enabled ? 'green' : 'red'">{{ u.enabled ? '启用' : '停用' }}</span></td>
                    <td class="row" style="gap:4px">
                      <button class="btn ghost sm" @click="editUser(u)">编辑</button>
                      <button class="btn ghost sm" @click="toggleUserEnabled(u)">{{ u.enabled ? '停用' : '启用' }}</button>
                      <button class="btn ghost sm danger" @click="deleteUser(u.id)">删除</button>
                    </td>
                  </tr>
                  <tr v-if="filteredUsers.length === 0"><td colspan="7" class="empty-state">暂无数据</td></tr>
                </tbody>
              </table>
            </div>
            <Pagination :total="filteredUsers.length" :page="userPage" @change="userPage = $event" />
          </div>

          <!-- Classes -->
          <div v-if="activeNavKey === 'classes'" class="card" id="classPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/></svg>
                </div>
                班级列表
              </h3>
              <div class="row">
                <input v-model="adminClassFilter" class="input compact" placeholder="搜索班级…" />
                <button class="btn ghost sm" @click="loadAdmin">刷新</button>
                <button class="btn ghost sm danger" :disabled="selectedClassIds.length === 0" @click="deleteSelectedClasses">
                  批量删除<span v-if="selectedClassIds.length">（{{ selectedClassIds.length }}）</span>
                </button>
              </div>
            </div>
            <div class="table-wrap">
              <table class="table">
                <thead>
                  <tr>
                    <th>
                      <input type="checkbox" :checked="allClassesOnPageSelected" :disabled="paginatedClasses.length === 0" @change="toggleClassPageSelection($event.target.checked)" />
                    </th>
                    <th>班级</th><th>院系</th><th>专业</th><th>学生规模</th><th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="c in paginatedClasses" :key="c.id">
                    <td>
                      <input type="checkbox" :checked="selectedClassIds.includes(c.id)" @change="toggleClassSelection(c.id)" />
                    </td>
                    <td>{{ c.name }}</td><td>{{ c.department }}</td><td>{{ c.major }}</td><td>{{ c.studentCount || 0 }}</td>
                    <td class="row" style="gap:4px">
                      <button class="btn ghost sm" @click="editClass(c)">编辑</button>
                      <button class="btn ghost sm danger" @click="deleteClass(c.id)">删除</button>
                    </td>
                  </tr>
                  <tr v-if="filteredClasses.length === 0"><td colspan="6" class="empty-state">暂无数据</td></tr>
                </tbody>
              </table>
            </div>
            <Pagination :total="filteredClasses.length" :page="classPage" @change="classPage = $event" />
          </div>
        </div>

        <!-- Logs -->
        <div v-if="activeNavKey === 'logs'" class="card" id="logsPanel">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              </div>
              系统日志
            </h3>
            <div class="row">
              <input v-model="adminLogFilter" class="input compact" placeholder="搜索日志…" />
              <button class="btn ghost sm" @click="loadAdmin">刷新</button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>时间</th><th>账号</th><th>动作</th><th>对象</th><th>详情</th></tr></thead>
              <tbody>
                <tr v-for="log in paginatedLogs" :key="log.id">
                  <td>{{ log.createdAt }}</td><td>{{ log.actorUsername }}</td><td>{{ log.action }}</td><td>{{ log.targetType }}</td><td>{{ log.detail }}</td>
                </tr>
                <tr v-if="filteredLogs.length === 0"><td colspan="5" class="empty-state">暂无日志</td></tr>
              </tbody>
            </table>
          </div>
          <Pagination :total="filteredLogs.length" :page="logPage" @change="logPage = $event" page-size="20" />
        </div>
      </div>

      <!-- ═══ TEACHER TAB ═══ -->
      <div v-if="tab==='teacher' && isTeacher && activeNavKey !== 'overview'" class="grid module-page">
        <div v-if="activeNavKey === 'questions' || activeNavKey === 'paper-build'" class="grid" :class="{ 'grid-2': activeNavKey === 'questions' }">
          <div v-if="activeNavKey === 'questions'" class="card panel-card" id="questionFormCard">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
                </div>
                题库维护
              </h3>
              <div class="row">
                <input v-model="teacherQuestionFilter" class="input compact" placeholder="搜索题目…" />
                <button class="btn ghost sm" @click="loadTeacher">刷新</button>
                <button class="btn ghost sm danger" :disabled="selectedQuestionIds.length === 0" @click="deleteSelectedQuestions">
                  批量删除<span v-if="selectedQuestionIds.length">（{{ selectedQuestionIds.length }}）</span>
                </button>
              </div>
            </div>
            <div class="taxonomy-strip">
              <div v-for="card in questionTaxonomyCards" :key="card.label" class="taxonomy-card">
                <span>{{ card.label }}</span>
                <strong>{{ card.value }}</strong>
                <small>{{ card.tip }}</small>
              </div>
            </div>
            <div class="identity-summary">
              <div>
                <span>题库档案粒度</span>
                <strong>学科门类 / 课程代码 / 章节小节 / 知识模块 / 能力层级 / 来源标签</strong>
              </div>
              <button class="btn ghost sm" @click="teacher.questionForm = blankQuestionForm()">新建题目</button>
            </div>
            <div class="grid question-form-grid">
              <div class="form-group full-span"><label class="form-label">题干</label><textarea class="textarea title-input" v-model="teacher.questionForm.title" placeholder="录入完整题干，建议包含必要场景、约束和输出要求"></textarea></div>
              <div class="form-group"><label class="form-label">题型</label><select class="select" v-model="teacher.questionForm.type"><option v-for="item in questionTypeOptions" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">难度</label><select class="select" v-model="teacher.questionForm.difficulty"><option v-for="item in difficultyOptions" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">分值</label><input class="input" v-model.number="teacher.questionForm.score" type="number" min="1" placeholder="5" /></div>
              <div class="form-group"><label class="form-label">预计用时（分钟）</label><input class="input" v-model.number="teacher.questionForm.estimatedMinutes" type="number" min="1" placeholder="3" /></div>

              <div class="form-group"><label class="form-label">学科门类</label><input class="input" v-model="teacher.questionForm.subject" list="subjectOptions" placeholder="如 计算机类" /></div>
              <div class="form-group"><label class="form-label">课程代码</label><input class="input" v-model="teacher.questionForm.courseCode" list="courseCodeOptions" placeholder="如 CS-JAVA" /></div>
              <div class="form-group"><label class="form-label">课程名称</label><input class="input" v-model="teacher.questionForm.courseName" list="courseNameOptions" placeholder="如 Java 程序设计" /></div>
              <div class="form-group"><label class="form-label">章节</label><input class="input" v-model="teacher.questionForm.chapter" list="chapterOptions" placeholder="如 第 03 章 面向对象基础" /></div>
              <div class="form-group"><label class="form-label">小节</label><input class="input" v-model="teacher.questionForm.section" list="sectionOptions" placeholder="如 3.2 继承与多态" /></div>
              <div class="form-group"><label class="form-label">知识模块</label><input class="input" v-model="teacher.questionForm.knowledgeModule" list="moduleOptions" placeholder="如 面向对象编程" /></div>
              <div class="form-group"><label class="form-label">知识点</label><input class="input" v-model="teacher.questionForm.knowledgePoint" list="knowledgePointOptions" placeholder="如 方法重写" /></div>
              <div class="form-group"><label class="form-label">能力层级</label><select class="select" v-model="teacher.questionForm.cognitiveLevel"><option v-for="item in cognitiveLevelOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
              <div class="form-group"><label class="form-label">题目来源</label><input class="input" v-model="teacher.questionForm.source" list="sourceOptions" placeholder="自建题库 / 历年真题 / 教材例题" /></div>
              <div class="form-group"><label class="form-label">标签（逗号分隔）</label><input class="input" v-model="teacher.questionForm.tags" list="tagOptions" placeholder="核心考点, 易错题, 期末复习" /></div>

              <div class="form-group full-span"><label class="form-label">选项（用 | 分隔）</label><input class="input" v-model="teacher.questionForm.options" placeholder="A. 选项1 | B. 选项2 | C. 选项3 | D. 选项4" /></div>
              <div class="form-group"><label class="form-label">正确答案</label><input class="input" v-model="teacher.questionForm.correctAnswer" placeholder="A 或 A,B" /></div>
              <div class="form-group full-span"><label class="form-label">解析</label><textarea class="textarea" v-model="teacher.questionForm.explanation" placeholder="答案解析、评分要点或代码题参考思路"></textarea></div>

              <div class="form-actions">
                <button class="btn" @click="saveQuestion">{{ teacher.questionForm.id ? '更新题目（#'+teacher.questionForm.id+'）' : '保存题目' }}</button>
                <button class="btn ghost" @click="teacher.questionForm = blankQuestionForm()">重置</button>
              </div>
              <div class="form-actions import-actions">
                <input type="file" @change="onImportFile" style="font-size:12px" />
                <button class="btn ghost sm" @click="downloadQuestionTemplate">下载专业模板</button>
                <button class="btn ghost sm" @click="importQuestions">导入题目</button>
              </div>
            </div>
            <datalist id="subjectOptions"><option v-for="item in uniqueQuestionValues('subject')" :key="item" :value="item"></option></datalist>
            <datalist id="courseCodeOptions"><option v-for="item in uniqueQuestionValues('courseCode')" :key="item" :value="item"></option></datalist>
            <datalist id="courseNameOptions"><option v-for="item in uniqueQuestionValues('courseName')" :key="item" :value="item"></option></datalist>
            <datalist id="chapterOptions"><option v-for="item in uniqueQuestionValues('chapter')" :key="item" :value="item"></option></datalist>
            <datalist id="sectionOptions"><option v-for="item in uniqueQuestionValues('section')" :key="item" :value="item"></option></datalist>
            <datalist id="moduleOptions"><option v-for="item in uniqueQuestionValues('knowledgeModule')" :key="item" :value="item"></option></datalist>
            <datalist id="knowledgePointOptions"><option v-for="item in uniqueQuestionValues('knowledgePoint')" :key="item" :value="item"></option></datalist>
            <datalist id="sourceOptions"><option v-for="item in uniqueQuestionValues('source')" :key="item" :value="item"></option></datalist>
            <datalist id="tagOptions"><option v-for="item in questionTagOptions" :key="item" :value="item"></option></datalist>
          </div>

          <!-- Paper Assembly -->
          <div v-if="activeNavKey === 'paper-build'" class="card panel-card" id="paperBuildPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
                </div>
                组卷
              </h3>
              <span class="badge cyan">支持手动 / 自动</span>
            </div>
            <div class="grid">
              <div class="form-group"><label class="form-label">试卷标题</label><input class="input" v-model="teacher.manualForm.title" placeholder="如：2025 期中测验" /></div>
              <div class="grid grid-3">
                <div class="form-group"><label class="form-label">时长(分钟)</label><input class="input" v-model.number="teacher.manualForm.durationMinutes" type="number" /></div>
                <div class="form-group"><label class="form-label">及格线</label><input class="input" v-model.number="teacher.manualForm.passScore" type="number" /></div>
              </div>
              <div class="grid grid-2">
                <div class="form-group"><label class="form-label">开始时间</label><input class="input" v-model="teacher.manualForm.startTime" type="datetime-local" /></div>
                <div class="form-group"><label class="form-label">结束时间</label><input class="input" v-model="teacher.manualForm.endTime" type="datetime-local" /></div>
              </div>
              <div class="form-group"><label class="form-label">题目ID（逗号分隔）</label><input class="input" v-model="teacher.manualForm.questionIds" placeholder="1,2,5,8" /></div>
              <div class="form-group"><label class="form-label">目标班级（逗号分隔）</label><input class="input" v-model="teacher.manualForm.targetClasses" placeholder="留空表示全部班级" /></div>
              <button class="btn" @click="createManualPaper">生成手动试卷</button>
            </div>
            <hr style="border:0;border-top:1px solid var(--border);margin:14px 0" />
            <div class="auto-paper-head">
              <div>
                <h4>自动组卷</h4>
                <p>按课程、章节、知识模块、能力层级和标签抽题，当前条件命中 <strong>{{ autoPaperPool.length }}</strong> 题。</p>
              </div>
              <button class="btn ghost sm" @click="teacher.autoForm = blankAutoForm()">重置条件</button>
            </div>
            <div class="grid">
              <div class="form-group"><label class="form-label">试卷标题</label><input class="input" v-model="teacher.autoForm.title" placeholder="如：自动生成试卷" /></div>
              <div class="grid grid-3">
                <div class="form-group"><label class="form-label">时长(分钟)</label><input class="input" v-model.number="teacher.autoForm.durationMinutes" type="number" /></div>
                <div class="form-group"><label class="form-label">及格线</label><input class="input" v-model.number="teacher.autoForm.passScore" type="number" /></div>
                <div class="form-group"><label class="form-label">抽题数量</label><input class="input" v-model.number="teacher.autoForm.questionCount" type="number" /></div>
              </div>
              <div class="grid grid-2">
                <div class="form-group"><label class="form-label">开始时间</label><input class="input" v-model="teacher.autoForm.startTime" type="datetime-local" /></div>
                <div class="form-group"><label class="form-label">结束时间</label><input class="input" v-model="teacher.autoForm.endTime" type="datetime-local" /></div>
              </div>
              <div class="grid grid-4">
                <div class="form-group"><label class="form-label">学科门类</label><input class="input" v-model="teacher.autoForm.subject" list="subjectOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">课程代码</label><input class="input" v-model="teacher.autoForm.courseCode" list="courseCodeOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">课程名称</label><input class="input" v-model="teacher.autoForm.courseName" list="courseNameOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">章节</label><input class="input" v-model="teacher.autoForm.chapter" list="chapterOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">小节</label><input class="input" v-model="teacher.autoForm.section" list="sectionOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">知识模块</label><input class="input" v-model="teacher.autoForm.knowledgeModule" list="moduleOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">知识点</label><input class="input" v-model="teacher.autoForm.knowledgePoint" list="knowledgePointOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">题目来源</label><input class="input" v-model="teacher.autoForm.source" list="sourceOptions" placeholder="不限" /></div>
                <div class="form-group"><label class="form-label">难度</label><select class="select" v-model="teacher.autoForm.difficulty"><option value="">不限</option><option v-for="item in difficultyOptions" :key="item" :value="item">{{ item }}</option></select></div>
                <div class="form-group"><label class="form-label">题型</label><select class="select" v-model="teacher.autoForm.type"><option value="">不限</option><option v-for="item in questionTypeOptions" :key="item" :value="item">{{ item }}</option></select></div>
                <div class="form-group"><label class="form-label">能力层级</label><select class="select" v-model="teacher.autoForm.cognitiveLevel"><option value="">不限</option><option v-for="item in cognitiveLevelOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
                <div class="form-group"><label class="form-label">标签（需全部命中）</label><input class="input" v-model="teacher.autoForm.tags" list="tagOptions" placeholder="核心考点, 易错题" /></div>
              </div>
              <div class="form-group"><label class="form-label">目标班级（逗号分隔）</label><input class="input" v-model="teacher.autoForm.targetClasses" placeholder="留空表示全部班级" /></div>
              <div class="auto-pool-preview">
                <span :class="autoPaperPool.length >= Number(teacher.autoForm.questionCount || 0) ? 'badge green' : 'badge red'">
                  {{ autoPaperPool.length >= Number(teacher.autoForm.questionCount || 0) ? '题量充足' : '题量不足' }}
                </span>
                <span class="muted">后端会在命中题池中随机抽取 {{ teacher.autoForm.questionCount || 0 }} 题。</span>
              </div>
              <button class="btn" @click="createAutoPaper">生成自动试卷</button>
            </div>
          </div>
        </div>

        <!-- Question List -->
        <div v-if="activeNavKey === 'questions'" class="card" id="questionListPanel">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/></svg>
              </div>
              题库（{{ filteredQuestions.length }} 题）
            </h3>
            <div class="row">
              <input v-model="teacherQuestionFilter" class="input compact" placeholder="搜索题目…" />
              <button class="btn ghost sm" @click="loadTeacher">刷新</button>
              <select v-model="batchQuestionDifficulty" class="select compact" style="min-width: 120px">
                <option value="">批量难度</option>
                <option v-for="item in difficultyOptions" :key="`batch-difficulty-${item}`" :value="item">{{ item }}</option>
              </select>
              <button class="btn ghost sm" :disabled="selectedQuestionIds.length === 0 || !batchQuestionDifficulty" @click="updateSelectedQuestionDifficulty">
                批量改难度
              </button>
              <select v-model="batchQuestionType" class="select compact" style="min-width: 140px">
                <option value="">批量题型</option>
                <option v-for="item in questionTypeOptions" :key="`batch-type-${item}`" :value="item">{{ item }}</option>
              </select>
              <button class="btn ghost sm" :disabled="selectedQuestionIds.length === 0 || !batchQuestionType" @click="updateSelectedQuestionType">
                批量改题型
              </button>
              <button class="btn ghost sm danger" :disabled="selectedQuestionIds.length === 0" @click="deleteSelectedQuestions">
                批量删除<span v-if="selectedQuestionIds.length">（{{ selectedQuestionIds.length }}）</span>
              </button>
            </div>
          </div>
          <div class="question-filter-panel">
            <div class="filter-head">
              <div>
                <span class="eyebrow">Question Taxonomy</span>
                <strong>分类筛选</strong>
              </div>
              <span class="filter-count">已启用 {{ questionFilterActiveCount }} 个条件</span>
            </div>
            <div class="grid grid-4">
              <div class="form-group"><label class="form-label">学科门类</label><select class="select" v-model="questionFilters.subject"><option value="">全部</option><option v-for="item in uniqueQuestionValues('subject')" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">课程名称</label><select class="select" v-model="questionFilters.courseName"><option value="">全部</option><option v-for="item in uniqueQuestionValues('courseName')" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">章节</label><select class="select" v-model="questionFilters.chapter"><option value="">全部</option><option v-for="item in uniqueQuestionValues('chapter')" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">知识模块</label><select class="select" v-model="questionFilters.knowledgeModule"><option value="">全部</option><option v-for="item in uniqueQuestionValues('knowledgeModule')" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">题型</label><select class="select" v-model="questionFilters.type"><option value="">全部</option><option v-for="item in questionTypeOptions" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">难度</label><select class="select" v-model="questionFilters.difficulty"><option value="">全部</option><option v-for="item in difficultyOptions" :key="item" :value="item">{{ item }}</option></select></div>
              <div class="form-group"><label class="form-label">能力层级</label><select class="select" v-model="questionFilters.cognitiveLevel"><option value="">全部</option><option v-for="item in cognitiveLevelOptions" :key="item.value" :value="item.value">{{ item.label }}</option></select></div>
              <div class="form-group"><label class="form-label">标签</label><select class="select" v-model="questionFilters.tag"><option value="">全部</option><option v-for="item in questionTagOptions" :key="item" :value="item">{{ item }}</option></select></div>
            </div>
            <div class="row">
              <button class="btn ghost sm" @click="resetQuestionFilters">清空筛选</button>
              <span class="muted">搜索会同时匹配题干、课程、章节、知识点、来源和标签。</span>
            </div>
          </div>
          <div class="question-matrix">
            <button
              v-for="item in questionMatrix"
              :key="item.key"
              class="matrix-card"
              @click="questionFilters.courseName = item.courseName"
            >
              <span>{{ item.courseName }}</span>
              <strong>{{ item.count }}</strong>
              <small>{{ item.modules }} 个模块 · {{ item.hard }} 道高难</small>
            </button>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>
                    <input type="checkbox" :checked="allQuestionsOnPageSelected" :disabled="paginatedQuestions.length === 0" @change="toggleQuestionPageSelection($event.target.checked)" />
                  </th>
                  <th>ID</th><th>题目</th><th>课程路径</th><th>知识定位</th><th>类型/难度</th><th>分值/用时</th><th>标签</th><th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="q in paginatedQuestions" :key="'q'+q.id">
                  <td>
                    <input type="checkbox" :checked="selectedQuestionIds.includes(q.id)" @change="toggleQuestionSelection(q.id)" />
                  </td>
                  <td><span class="badge">{{ q.id }}</span></td>
                  <td>
                    <div class="question-title">{{ q.title }}</div>
                    <div class="taxonomy-meta">{{ questionIdentity(q) }}</div>
                  </td>
                  <td>
                    <div class="question-path">{{ questionPath(q) }}</div>
                    <span class="course-pill">{{ q.courseCode || 'NO-CODE' }}</span>
                  </td>
                  <td>
                    <div class="question-path">{{ [q.knowledgeModule, q.knowledgePoint].filter(Boolean).join(' / ') || '未定位知识点' }}</div>
                    <div class="taxonomy-meta">{{ q.cognitiveLevel || '未设置能力层级' }}</div>
                  </td>
                  <td>
                    <span class="badge">{{ q.type }}</span>
                    <span class="badge" :class="q.difficulty === 'EASY' ? 'green' : q.difficulty === 'MEDIUM' ? 'amber' : 'red'">{{ q.difficulty }}</span>
                  </td>
                  <td>{{ q.score || 0 }} 分 · {{ q.estimatedMinutes || '-' }} 分钟</td>
                  <td>
                    <div class="tag-list" v-if="normalizeTags(q.tags).length">
                      <span v-for="tag in normalizeTags(q.tags).slice(0, 3)" :key="tag">{{ tag }}</span>
                    </div>
                    <span v-else class="muted">未打标签</span>
                  </td>
                  <td class="row" style="gap:4px">
                    <button class="btn ghost sm" @click="editQuestion(q)">编辑</button>
                    <button class="btn ghost sm danger" @click="deleteQuestion(q.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="filteredQuestions.length === 0">
                  <td colspan="9" class="empty-state">暂无题目，请先创建题目或调整筛选条件</td>
                </tr>
              </tbody>
            </table>
          </div>
          <Pagination :total="filteredQuestions.length" :page="questionListPage" @change="questionListPage = $event" />
        </div>

        <!-- Paper List -->
        <div v-if="activeNavKey === 'papers'" class="card">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon" style="background:#F0FDF4;color:var(--accent)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
              </div>
              试卷管理（{{ filteredPapers.length }} 份）
            </h3>
            <div class="row">
              <input v-model="teacherPaperFilter" class="input compact" placeholder="搜索试卷…" />
              <button class="btn ghost sm" @click="loadTeacher">刷新</button>
              <button class="btn ghost sm danger" :disabled="selectedPaperIds.length === 0" @click="deleteSelectedPapers">
                批量删除<span v-if="selectedPaperIds.length">（{{ selectedPaperIds.length }}）</span>
              </button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>
                    <input type="checkbox" :checked="allPapersOnPageSelected" :disabled="paginatedPapers.length === 0" @change="togglePaperPageSelection($event.target.checked)" />
                  </th>
                  <th>ID</th><th>试卷标题</th><th>生成方式</th><th>总分</th><th>时长</th><th>状态</th><th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="p in paginatedPapers" :key="'p'+p.id" :style="p.published ? 'background:var(--primary-light)' : ''">
                  <td>
                    <input type="checkbox" :checked="selectedPaperIds.includes(p.id)" @change="togglePaperSelection(p.id)" />
                  </td>
                  <td><span class="badge cyan">{{ p.id }}</span></td>
                  <td><b>{{ p.title }}</b></td>
                  <td><span class="badge blue">{{ p.autoGenerated ? '自动' : '手动' }}</span></td>
                  <td>{{ p.totalScore }} 分</td>
                  <td>{{ p.durationMinutes }} 分钟</td>
                  <td><span class="badge" :class="p.published ? 'green' : 'amber'">{{ p.published ? '已发布' : '未发布' }}</span></td>
                  <td class="row" style="gap:4px">
                    <button class="btn ghost sm" @click="openPaperDetail(p.id)">详情</button>
                    <button class="btn sm" v-if="!p.published" @click="publishPaper(p.id)">发布</button>
                    <button class="btn ghost sm" @click="loadMonitor(p.id)">监考</button>
                    <button class="btn ghost sm" @click="loadAnalytics(p.id)">分析</button>
                    <button class="btn ghost sm" @click="exportScores(p.id)">导出</button>
                    <button class="btn ghost sm danger" @click="deletePaper(p.id)">删除</button>
                  </td>
                </tr>
                <tr v-if="filteredPapers.length === 0">
                  <td colspan="8" class="empty-state">
                    <p>暂无试卷，请使用上方组卷功能创建试卷</p>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <Pagination :total="filteredPapers.length" :page="paperListPage" @change="paperListPage = $event" />
        </div>

        <!-- Exam Monitor -->
        <div v-if="activeNavKey === 'monitor' && teacher.monitor" class="card command-card" id="monitorPanel">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l4-4 3 3 5-7"/></svg>
              </div>
              实时监考台：{{ teacher.monitor.title }}
            </h3>
            <div class="row">
              <input v-model="teacherMonitorFilter" class="input compact" placeholder="搜索学生/班级/状态…" />
              <button class="btn ghost sm" @click="loadMonitor(teacher.monitorPaperId)">刷新监考</button>
            </div>
          </div>
          <div class="monitor-metrics">
            <div><span>参与人次</span><strong>{{ teacher.monitor.attemptCount }}</strong></div>
            <div><span>进行中</span><strong>{{ teacher.monitor.activeCount }}</strong></div>
            <div><span>已交卷</span><strong>{{ teacher.monitor.submittedCount }}</strong></div>
            <div><span>异常答卷</span><strong>{{ teacher.monitor.suspiciousCount }}</strong></div>
            <div><span>待阅卷</span><strong>{{ teacher.monitor.reviewPendingCount }}</strong></div>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>考生</th><th>班级</th><th>状态</th><th>分数</th><th>剩余</th><th>切屏</th><th>阅卷</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="a in filteredMonitorAttempts" :key="a.attemptId">
                  <td><b>{{ a.studentUsername }}</b></td>
                  <td>{{ a.className }}</td>
                  <td><span class="badge" :class="statusClass(a.status)">{{ a.status }}</span></td>
                  <td>{{ a.score }} / {{ a.totalScore }}</td>
                  <td>{{ formatSeconds(a.remainingSeconds) }}</td>
                  <td><span class="badge" :class="a.suspicious ? 'red' : a.switchCount > 0 ? 'amber' : 'green'">{{ a.switchCount }}</span></td>
                  <td><span class="badge" :class="a.needsReview ? 'amber' : 'green'">{{ a.needsReview ? '待阅卷' : '已处理' }}</span></td>
                  <td class="row" style="gap:4px">
                    <button class="btn ghost sm" @click="openAttemptDetail(a.attemptId)">答卷</button>
                    <button v-if="a.status === 'IN_PROGRESS'" class="btn ghost sm" @click="extendAttempt(a.attemptId, 10)">延时10分</button>
                  </td>
                </tr>
                <tr v-if="filteredMonitorAttempts.length === 0"><td colspan="8" class="empty-state">暂无考生记录</td></tr>
              </tbody>
            </table>
          </div>
        </div>

        <div v-if="activeNavKey === 'monitor' && !teacher.monitor" class="card module-empty">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 3v18h18"/><path d="M7 15l4-4 3 3 5-7"/></svg>
              </div>
              选择试卷进入监考
            </h3>
            <button class="btn ghost sm" @click="loadTeacher">刷新试卷</button>
          </div>
          <div v-if="filteredPapers.length" class="module-picker-grid">
            <button v-for="p in filteredPapers" :key="'monitor-pick'+p.id" class="module-picker-card" @click="loadMonitor(p.id)">
              <span class="badge" :class="p.published ? 'green' : 'amber'">{{ p.published ? '已发布' : '未发布' }}</span>
              <strong>{{ p.title }}</strong>
              <small>{{ p.durationMinutes }} 分钟 · {{ p.totalScore }} 分 · {{ p.autoGenerated ? '自动组卷' : '手动组卷' }}</small>
            </button>
          </div>
          <div v-else class="empty-state">
            <p>暂无可监考试卷，请先完成组卷并发布。</p>
            <button class="btn" @click="goSection(navItems.find(item => item.key === 'paper-build'))">去组卷</button>
          </div>
        </div>

        <!-- Analytics -->
        <div v-if="activeNavKey === 'analytics' && teacher.analytics" class="card" id="analyticsPanel">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
              </div>
              成绩分析
            </h3>
            <span class="badge green">教学分析</span>
          </div>
          <div class="grid grid-4" style="margin-bottom:14px">
            <div class="stat-card">
              <div class="stat-label">平均分</div>
              <div class="stat-value">{{ Math.round(teacher.analytics.averageScore || 0) }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">最高分</div>
              <div class="stat-value">{{ teacher.analytics.maxScore }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">最低分</div>
              <div class="stat-value">{{ teacher.analytics.minScore }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">及格率</div>
              <div class="stat-value">{{ formatPercent(teacher.analytics.passRate) }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">答卷数</div>
              <div class="stat-value">{{ teacher.analytics.attemptCount }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">待阅卷</div>
              <div class="stat-value">{{ teacher.analytics.reviewPendingCount }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">异常卷</div>
              <div class="stat-value">{{ teacher.analytics.suspiciousCount }}</div>
            </div>
          </div>
          <!-- Chart bars for question correct rates -->
          <div class="chart-bar-wrap" style="height:140px">
            <div class="chart-bar" v-for="r in teacher.analytics.questionRates" :key="r.questionId"
              :style="{ height: (r.correctRate || 0) * 100 + '%', background: r.correctRate > 0.7 ? 'var(--accent)' : r.correctRate > 0.4 ? 'var(--warning)' : 'var(--danger)' }"
              :title="r.title + ': ' + formatPercent(r.correctRate)">
              <div class="chart-bar-value">{{ formatPercent(r.correctRate) }}</div>
              <div class="chart-bar-label">{{ r.title?.slice(0, 8) }}{{ r.title?.length > 8 ? '…' : '' }}</div>
            </div>
          </div>
          <div class="grid grid-2" style="margin-top:24px">
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>题目</th><th>知识点</th><th>正确率</th><th>进度</th></tr></thead>
                <tbody>
                  <tr v-for="r in teacher.analytics.questionRates" :key="r.questionId">
                    <td>{{ r.title }}</td>
                    <td><span class="badge">{{ r.knowledgePoint || '未分类' }}</span></td>
                    <td>{{ formatPercent(r.correctRate) }}</td>
                    <td>
                      <div class="progress-bar">
                        <div class="progress-bar-fill" :style="{ width: (r.correctRate || 0) * 100 + '%', background: r.correctRate > 0.7 ? 'var(--accent)' : r.correctRate > 0.4 ? 'var(--warning)' : 'var(--danger)' }"></div>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>班级</th><th>均分</th><th>答卷</th><th>及格率</th></tr></thead>
                <tbody>
                  <tr v-for="c in teacher.analytics.classScores" :key="c.className">
                    <td>{{ c.className }}</td>
                    <td><strong>{{ Math.round(c.averageScore || 0) }}</strong></td>
                    <td>{{ c.attemptCount }}</td>
                    <td>{{ formatPercent(c.passRate) }}</td>
                  </tr>
                  <tr v-if="!teacher.analytics.classScores?.length"><td colspan="4" class="empty-state">暂无班级数据</td></tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="knowledge-grid" v-if="teacher.analytics.knowledgePoints?.length">
            <div v-for="k in teacher.analytics.knowledgePoints" :key="k.knowledgePoint" class="knowledge-card">
              <div class="row-between">
                <strong>{{ k.knowledgePoint }}</strong>
                <span class="badge" :class="k.correctRate > 0.7 ? 'green' : k.correctRate > 0.4 ? 'amber' : 'red'">{{ formatPercent(k.correctRate) }}</span>
              </div>
              <div class="progress-bar">
                <div class="progress-bar-fill" :style="{ width: (k.correctRate || 0) * 100 + '%', background: k.correctRate > 0.7 ? 'var(--accent)' : k.correctRate > 0.4 ? 'var(--warning)' : 'var(--danger)' }"></div>
              </div>
              <div class="muted">已统计 {{ k.answeredCount }} 次作答</div>
            </div>
          </div>
        </div>

        <div v-if="activeNavKey === 'analytics' && !teacher.analytics" class="card module-empty">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
              </div>
              选择试卷查看分析
            </h3>
            <button class="btn ghost sm" @click="loadTeacher">刷新试卷</button>
          </div>
          <div v-if="filteredPapers.length" class="module-picker-grid">
            <button v-for="p in filteredPapers" :key="'analytics-pick'+p.id" class="module-picker-card" @click="loadAnalytics(p.id)">
              <span class="badge" :class="p.published ? 'green' : 'amber'">{{ p.published ? '可统计' : '未发布' }}</span>
              <strong>{{ p.title }}</strong>
              <small>{{ p.totalScore }} 分 · 及格线 {{ p.passScore || 60 }} · {{ p.targetClasses?.join('、') || '全部班级' }}</small>
            </button>
          </div>
          <div v-else class="empty-state">
            <p>暂无试卷数据，请先创建试卷。</p>
            <button class="btn" @click="goSection(navItems.find(item => item.key === 'paper-build'))">去组卷</button>
          </div>
        </div>
      </div>

      <!-- ═══ STUDENT TAB ═══ -->
      <div v-if="tab==='student' && isStudent && activeNavKey !== 'overview'" class="grid module-page">
        <div v-if="activeNavKey === 'upcoming' || activeNavKey === 'transcript' || activeNavKey === 'wrong-book'" class="grid">
          <!-- Upcoming -->
          <div v-if="activeNavKey === 'upcoming'" class="card" id="upcomingPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                </div>
                待考考试
              </h3>
              <div class="row">
                <input v-model="studentPaperFilter" class="input compact" placeholder="搜索…" />
                <button class="btn ghost sm" @click="loadStudent">刷新</button>
              </div>
            </div>
            <div v-if="filteredUpcoming.length === 0" class="empty-state">
              <p>暂无待考考试</p>
            </div>
            <div v-for="paper in filteredUpcoming" :key="paper.id" class="paper-card">
              <div class="row-between">
                <div>
                  <b>{{ paper.title }}</b>
                  <div class="muted">{{ paper.durationMinutes }} 分钟 · 满分 {{ paper.totalScore }} 分</div>
                  <div class="muted">{{ paperTimeRange(paper) }}</div>
                </div>
                <span class="badge" :class="isPaperOpen(paper) ? 'green' : 'amber'">{{ paperStatusLabel(paper) }}</span>
              </div>
              <button class="btn" :disabled="!isPaperOpen(paper)" @click="startExam(paper.id)" style="margin-top:4px">{{ paperActionLabel(paper) }}</button>
            </div>
          </div>

          <!-- Transcript -->
          <div v-if="activeNavKey === 'transcript'" class="card" id="transcriptPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
                </div>
                成绩单
              </h3>
              <button class="btn ghost sm" @click="loadStudent">刷新</button>
            </div>
            <div v-if="student.stats.trend.length === 0" class="empty-state"><p>暂无成绩数据</p></div>
            <div v-for="s in student.stats.trend" :key="s.label" class="row-between" style="padding:8px 0;border-bottom:1px solid var(--border)">
              <span class="badge">{{ s.label }}</span>
              <strong>{{ s.score }} 分</strong>
            </div>
            <div style="margin-top:10px;display:grid;gap:4px">
              <div class="muted">平均分：<strong>{{ student.stats.averageScore || 0 }}</strong></div>
              <div class="muted">答卷数：<strong>{{ student.stats.attemptCount || 0 }}</strong></div>
              <div class="muted">可疑卷：<strong>{{ student.stats.suspiciousCount || 0 }}</strong></div>
            </div>
            <div class="knowledge-grid compact" v-if="student.stats.knowledgePoints?.length">
              <div v-for="k in student.stats.knowledgePoints" :key="k.knowledgePoint" class="knowledge-card">
                <div class="row-between">
                  <strong>{{ k.knowledgePoint }}</strong>
                  <span class="badge" :class="k.correctRate > 0.7 ? 'green' : k.correctRate > 0.4 ? 'amber' : 'red'">{{ formatPercent(k.correctRate) }}</span>
                </div>
                <div class="progress-bar"><div class="progress-bar-fill" :style="{ width: (k.correctRate || 0) * 100 + '%', background: k.correctRate > 0.7 ? 'var(--accent)' : k.correctRate > 0.4 ? 'var(--warning)' : 'var(--danger)' }"></div></div>
              </div>
            </div>
          </div>

          <!-- Wrong Book -->
          <div v-if="activeNavKey === 'wrong-book'" class="card" id="wrongBookPanel">
            <div class="panel-head">
              <h3 class="section-title">
                <div class="section-icon">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"/><path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"/></svg>
                </div>
                错题本
              </h3>
              <button class="btn ghost sm" @click="loadStudent">刷新</button>
            </div>
            <div v-if="filteredWrongBook.length === 0" class="empty-state"><p>暂无错题，继续保持!</p></div>
            <div v-for="q in filteredWrongBook" :key="q.questionId" class="paper-card">
              <div class="row-between">
                <b>{{ q.title }}</b>
                <span class="badge red">错 {{ q.wrongCount }} 次</span>
              </div>
              <div class="row" style="gap:6px">
                <span class="badge">{{ q.subject || '综合' }}</span>
                <span class="badge">{{ q.knowledgePoint || '未分类' }}</span>
                <span class="badge">{{ q.type }}</span>
              </div>
              <div class="muted">正确答案：{{ q.correctAnswer }}</div>
              <button class="btn ghost sm" @click="startPractice(q)">进入练习</button>
            </div>
          </div>
        </div>

        <!-- History -->
        <div v-if="activeNavKey === 'history'" class="card" id="historyPanel">
          <div class="panel-head">
            <h3 class="section-title">
              <div class="section-icon">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
              </div>
              历史考试
            </h3>
            <div class="row">
              <input v-model="studentHistoryFilter" class="input compact" placeholder="搜索…" />
              <button class="btn ghost sm" @click="loadStudent">刷新</button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>试卷</th><th>状态</th><th>分数</th><th>及格</th><th>可疑</th><th>时间</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="a in paginatedHistory" :key="a.attemptId">
                  <td>{{ a.paperTitle }}</td>
                  <td><span class="badge" :class="statusClass(a.status)">{{ a.status }}</span></td>
                  <td><strong>{{ a.score }}</strong></td>
                  <td><span class="badge" :class="a.passed ? 'green' : 'red'">{{ a.passed ? '通过' : '未过' }}</span></td>
                  <td>{{ a.suspicious ? '是' : '否' }}</td>
                  <td>{{ a.submitTime || a.startTime }}</td>
                  <td><button class="btn ghost sm" @click="openAttemptDetail(a.attemptId)">查看答卷</button></td>
                </tr>
                <tr v-if="filteredHistory.length === 0"><td colspan="7" class="empty-state">暂无历史记录</td></tr>
              </tbody>
            </table>
          </div>
          <Pagination :total="filteredHistory.length" :page="historyPage" @change="historyPage = $event" />
        </div>
      </div>
        </main>
      </div>
    </template>

    <!-- ═══ EXAM PANEL ═══ -->
    <div v-if="currentExam" class="exam-panel">
      <div class="exam-header">
        <div class="row-between">
          <div>
            <h2 style="margin:0">{{ currentExam.paperTitle }}</h2>
            <div style="display:flex;align-items:center;gap:12px;margin-top:4px">
              <span class="badge" :class="remainingSeconds > 300 ? 'cyan' : remainingSeconds > 120 ? 'amber' : 'red'">
                剩余 {{ formattedRemaining }}
              </span>
              <span class="muted">切屏：{{ currentSwitchCount }}/{{ currentExam.switchLimit || 5 }}</span>
            </div>
          </div>
          <div class="row">
            <button class="btn ghost" @click="saveAll">保存全部</button>
            <button class="btn" @click="submitExam">交卷</button>
            <button class="btn ghost danger" @click="closeExam">关闭</button>
          </div>
        </div>
        <div class="timer-bar">
          <div class="timer-bar-fill" :class="remainingSeconds > 300 ? 'safe' : remainingSeconds > 120 ? 'warn' : 'danger'"
            :style="{ width: examTimerPercent + '%' }"></div>
        </div>
        <div class="question-nav" style="margin-top:12px">
          <div v-for="(q, idx) in currentQuestions" :key="q.id"
            class="nav-item" :class="{ done: answers[q.id], active: idx === currentIndex }"
            @click="currentIndex = idx">{{ idx + 1 }}</div>
        </div>
      </div>
      <div class="exam-body">
        <div v-for="(q, idx) in currentQuestions" :key="q.id" class="question" :class="{ current: idx === currentIndex }">
          <div class="row-between">
            <strong>{{ idx + 1 }}. {{ q.title }}</strong>
            <span class="badge cyan">{{ q.score }} 分</span>
          </div>
          <div class="row" style="margin:6px 0;gap:6px">
            <span class="badge">{{ q.type }}</span>
            <span class="badge">{{ q.subject }}</span>
            <span class="badge">{{ q.knowledgePoint }}</span>
            <span class="badge" :class="q.difficulty === 'EASY' ? 'green' : q.difficulty === 'MEDIUM' ? 'amber' : 'red'">{{ q.difficulty }}</span>
          </div>
          <div v-if="isChoiceQuestion(q.type) && q.options?.length" class="choice-grid">
            <button
              v-for="(option, optIndex) in q.options"
              :key="option"
              type="button"
              class="choice-option"
              :class="{ selected: isOptionSelected(q.id, option, optIndex) }"
              :aria-pressed="isOptionSelected(q.id, option, optIndex)"
              @click="selectOption(q, option, optIndex)"
            >
              <span class="choice-prefix">{{ optionKey(option, optIndex) }}</span>
              <span class="choice-text">{{ optionText(option, optIndex) }}</span>
            </button>
          </div>
          <ul v-else-if="q.options && q.options.length" style="padding-left:18px;margin:8px 0">
            <li v-for="option in q.options" :key="option" style="margin:4px 0">{{ option }}</li>
          </ul>
          <textarea
            v-if="needTextArea(q.type)"
            class="textarea"
            :value="answers[q.id] || ''"
            @input="handleAnswer(q.id, $event.target.value)"
            placeholder="请输入答案"
          />
          <input
            v-else-if="!isChoiceQuestion(q.type)"
            class="input"
            :value="answers[q.id] || ''"
            @input="handleAnswer(q.id, $event.target.value)"
            placeholder="请输入答案"
          />
          <div v-else class="choice-answer-preview">当前选择：{{ answers[q.id] || '未选择' }}</div>
        </div>
      </div>
    </div>

    <!-- ═══ PROFILE DRAWER ═══ -->
    <div v-if="openProfile" class="drawer-backdrop" @click.self="openProfile = false">
      <div class="drawer">
        <div class="panel-head">
          <h3 class="section-title">
            <div class="section-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            个人中心
          </h3>
          <button class="btn ghost sm" @click="openProfile = false">关闭</button>
        </div>
        <div class="drawer-section">
          <div class="detail-grid">
            <div><span>账号</span><strong>{{ user.username }}</strong></div>
            <div><span>姓名</span><strong>{{ user.realName }}</strong></div>
            <div><span>角色</span><strong>{{ user.role }}</strong></div>
          </div>
        </div>
        <div class="drawer-section">
          <h4 style="font-size:13px;margin-bottom:8px;color:var(--text-secondary)">修改密码</h4>
          <div class="grid">
            <div class="form-group"><label class="form-label">旧密码</label><input class="input" v-model="passwordForm.oldPassword" type="password" /></div>
            <div class="form-group"><label class="form-label">新密码</label><input class="input" v-model="passwordForm.newPassword" type="password" /></div>
            <button class="btn" @click="changePassword">修改密码</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ PAPER DETAIL DRAWER ═══ -->
    <div v-if="paperDetail" class="drawer-backdrop" @click.self="paperDetail = null">
      <div class="drawer wide">
        <div class="panel-head">
          <h3 class="section-title">
            <div class="section-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/></svg>
            </div>
            试卷详情
          </h3>
          <button class="btn ghost sm" @click="paperDetail = null">关闭</button>
        </div>
        <div class="drawer-section">
          <div class="detail-grid grid-4">
            <div><span>标题</span><strong>{{ paperDetail.meta.title }}</strong></div>
            <div><span>时长</span><strong>{{ paperDetail.meta.durationMinutes }} 分钟</strong></div>
            <div><span>及格线</span><strong>{{ paperDetail.meta.passScore }}</strong></div>
            <div><span>总分</span><strong>{{ paperDetail.meta.totalScore }}</strong></div>
          </div>
          <div class="muted" style="margin-top:8px">目标班级：{{ (paperDetail.meta.targetClasses || []).join('、') || '全部' }}</div>
        </div>
        <div class="drawer-section scroll">
          <div v-for="q in paperDetail.questions" :key="q.id" class="paper-card">
            <b>{{ q.title }}</b>
            <div class="row" style="gap:6px">
              <span class="badge">{{ q.type }}</span>
              <span class="badge">{{ q.subject }}</span>
              <span class="badge" :class="q.difficulty === 'EASY' ? 'green' : 'amber'">{{ q.difficulty }}</span>
            </div>
            <div class="muted">正确答案：{{ q.correctAnswer }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ ATTEMPT DETAIL DRAWER ═══ -->
    <div v-if="attemptDetail" class="drawer-backdrop" @click.self="attemptDetail = null">
      <div class="drawer wide">
        <div class="panel-head">
          <h3 class="section-title">
            <div class="section-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"/><line x1="8" y1="21" x2="16" y2="21"/><line x1="12" y1="17" x2="12" y2="21"/></svg>
            </div>
            答卷详情
          </h3>
          <button class="btn ghost sm" @click="attemptDetail = null">关闭</button>
        </div>
        <div class="drawer-section">
          <div class="detail-grid grid-4">
            <div><span>试卷</span><strong>{{ attemptDetail.paperTitle }}</strong></div>
            <div><span>考生</span><strong>{{ attemptDetail.studentUsername || '-' }}</strong></div>
            <div><span>分数</span><strong>{{ attemptDetail.score }} / {{ attemptDetail.totalScore || '-' }}</strong></div>
            <div><span>状态</span><strong>{{ attemptDetail.status }}</strong></div>
            <div><span>切屏</span><strong>{{ attemptDetail.switchCount }}</strong></div>
            <div><span>用时</span><strong>{{ formatSeconds(attemptDetail.durationSeconds) }}</strong></div>
            <div><span>是否通过</span><strong>{{ attemptDetail.passed ? '通过' : '未通过' }}</strong></div>
            <div><span>阅卷状态</span><strong>{{ attemptDetail.needsReview ? '待阅卷' : '已完成' }}</strong></div>
          </div>
        </div>
        <div class="drawer-section scroll">
          <div v-for="a in attemptDetail.answers" :key="a.questionId" class="paper-card">
            <div class="row-between">
              <b>{{ a.title || ('题目 #' + a.questionId) }}</b>
              <span class="badge" :class="a.reviewed ? (a.correct ? 'green' : 'red') : 'amber'">{{ a.reviewed ? (a.correct ? '正确' : '错误') : '待阅卷' }}</span>
            </div>
            <div class="row" style="gap:6px">
              <span class="badge">{{ a.type }}</span>
              <span class="badge">{{ a.subject || '综合' }}</span>
              <span class="badge">{{ a.knowledgePoint || '未分类' }}</span>
              <span class="badge cyan">{{ a.maxScore }} 分</span>
            </div>
            <div class="answer-block"><span>作答</span>{{ a.answerText || '未作答' }}</div>
            <div class="answer-block" v-if="a.correctAnswer"><span>参考答案</span>{{ a.correctAnswer }}</div>
            <div class="answer-block" v-if="a.explanation"><span>解析</span>{{ a.explanation }}</div>
            <div class="row-between">
              <div>得分：<strong>{{ a.score }}</strong> / {{ a.maxScore }}</div>
              <div v-if="isTeacher && needsManualReview(a)" class="review-box">
                <input class="input compact" v-model.number="a.draftScore" type="number" min="0" :max="a.maxScore" />
                <button class="btn sm" @click="reviewAnswer(a)">保存评分</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ WRONG QUESTION PRACTICE DRAWER ═══ -->
    <div v-if="practiceQuestion" class="drawer-backdrop" @click.self="practiceQuestion = null">
      <div class="drawer">
        <div class="panel-head">
          <h3 class="section-title">
            <div class="section-icon">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M12 4h9"/><path d="M4 9h16"/><path d="M4 15h16"/><path d="M4 4h.01"/><path d="M4 20h.01"/></svg>
            </div>
            错题强化练习
          </h3>
          <button class="btn ghost sm" @click="practiceQuestion = null">关闭</button>
        </div>
        <div class="drawer-section practice-card">
          <div class="row" style="gap:6px">
            <span class="badge">{{ practiceQuestion.type }}</span>
            <span class="badge">{{ practiceQuestion.subject || '综合' }}</span>
            <span class="badge">{{ practiceQuestion.knowledgePoint || '未分类' }}</span>
            <span class="badge red">错 {{ practiceQuestion.wrongCount }} 次</span>
          </div>
          <h3>{{ practiceQuestion.title }}</h3>
          <div v-if="isChoiceQuestion(practiceQuestion.type) && practiceQuestion.options?.length" class="choice-grid">
            <button
              v-for="(option, optIndex) in practiceQuestion.options"
              :key="option"
              type="button"
              class="choice-option"
              :class="{ selected: isPracticeOptionSelected(option, optIndex) }"
              :aria-pressed="isPracticeOptionSelected(option, optIndex)"
              @click="selectPracticeOption(option, optIndex)"
            >
              <span class="choice-prefix">{{ optionKey(option, optIndex) }}</span>
              <span class="choice-text">{{ optionText(option, optIndex) }}</span>
            </button>
          </div>
          <ul v-else-if="practiceQuestion.options?.length" class="option-list">
            <li v-for="option in practiceQuestion.options" :key="option">{{ option }}</li>
          </ul>
          <textarea v-if="needTextArea(practiceQuestion.type)" class="textarea" v-model="practiceAnswer" placeholder="写下你的答案"></textarea>
          <input v-else-if="!isChoiceQuestion(practiceQuestion.type)" class="input" v-model="practiceAnswer" placeholder="请输入答案" @keyup.enter="checkPractice" />
          <div v-else class="choice-answer-preview">当前选择：{{ practiceAnswer || '未选择' }}</div>
          <div class="row">
            <button class="btn" @click="checkPractice">检查答案</button>
            <button class="btn ghost" @click="practiceAnswer = ''; practiceResult = null">重做</button>
          </div>
          <div v-if="practiceResult !== null" :class="practiceResult ? 'notice' : 'err-msg'">
            {{ practiceResult ? '回答正确，可以继续保持。' : '回答不正确，请对照参考答案复盘。' }}
          </div>
          <div class="answer-block"><span>参考答案</span>{{ practiceQuestion.correctAnswer }}</div>
        </div>
      </div>
    </div>

    <!-- ═══ CONFIRM DIALOG ═══ -->
    <div v-if="dialog.open" class="dialog-backdrop" @click.self="cancelDialog">
      <div class="dialog-card" :class="'tone-' + dialog.tone" role="dialog" aria-modal="true" :aria-label="dialog.title">
        <div class="dialog-orbit">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <div class="dialog-icon">
          <svg v-if="dialog.tone === 'danger'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 9v4"/><path d="M12 17h.01"/><path d="M10.29 3.86 1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/></svg>
          <svg v-else width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M12 4h9"/><path d="M4 9h16"/><path d="M4 15h16"/><path d="M4 4h.01"/><path d="M4 20h.01"/></svg>
        </div>
        <div class="dialog-copy">
          <span class="eyebrow">{{ dialog.eyebrow }}</span>
          <h3>{{ dialog.title }}</h3>
          <p>{{ dialog.message }}</p>
          <div v-if="dialog.details?.length" class="dialog-details">
            <span v-for="item in dialog.details" :key="item">{{ item }}</span>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="btn ghost" @click="cancelDialog">{{ dialog.cancelText }}</button>
          <button class="btn" :class="{ danger: dialog.tone === 'danger' }" @click="confirmDialog">{{ dialog.confirmText }}</button>
        </div>
      </div>
    </div>

    <!-- ═══ TOAST ═══ -->
    <div v-if="message" :class="'floating-msg ' + (messageType === 'error' ? 'error' : 'notice')">
      <svg v-if="messageType === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="15" y1="9" x2="9" y2="15"/><line x1="9" y1="9" x2="15" y2="15"/></svg>
      <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import Pagination from './components/Pagination.vue'

const loginForm = reactive({ username: 'admin', password: 'admin123' })
const user = ref(JSON.parse(localStorage.getItem('exam-user') || 'null'))
const message = ref('')
const messageType = ref('notice')
const tab = ref(user.value?.role === 'ADMIN' ? 'admin' : user.value?.role === 'TEACHER' ? 'teacher' : 'student')
const globalSearch = ref('')
const openProfile = ref(false)
const passwordForm = reactive({ oldPassword: '', newPassword: '' })
const paperDetail = ref(null)
const attemptDetail = ref(null)
const loggingIn = ref(false)
const adminLoading = ref(false)
const adminUserFilter = ref('')
const adminUserRoleFilter = ref('')
const adminUserStatusFilter = ref('')
const adminClassFilter = ref('')
const adminLogFilter = ref('')
const teacherQuestionFilter = ref('')
const teacherPaperFilter = ref('')
const teacherMonitorFilter = ref('')
const studentPaperFilter = ref('')
const studentHistoryFilter = ref('')
const practiceQuestion = ref(null)
const practiceAnswer = ref('')
const practiceResult = ref(null)

const isDark = ref(false)
const sidebarOpen = ref(false)
const activeNavKey = ref('overview')
const showNotif = ref(false)
const notifications = ref([])
const selectedUserIds = ref([])
const selectedClassIds = ref([])
const selectedQuestionIds = ref([])
const selectedPaperIds = ref([])
const batchQuestionDifficulty = ref('')
const batchQuestionType = ref('')
const userPage = ref(1)
const classPage = ref(1)
const logPage = ref(1)
const questionListPage = ref(1)
const paperListPage = ref(1)
const historyPage = ref(1)
const pageSize = 10

const questionTypeOptions = ['SINGLE_CHOICE', 'MULTI_CHOICE', 'TRUE_FALSE', 'FILL_BLANK', 'SHORT_ANSWER', 'CODING']
const difficultyOptions = ['EASY', 'MEDIUM', 'HARD']
const cognitiveLevelOptions = [
  { value: 'REMEMBER', label: 'REMEMBER 记忆' },
  { value: 'UNDERSTAND', label: 'UNDERSTAND 理解' },
  { value: 'APPLY', label: 'APPLY 应用' },
  { value: 'ANALYZE', label: 'ANALYZE 分析' },
  { value: 'EVALUATE', label: 'EVALUATE 评价' },
  { value: 'CREATE', label: 'CREATE 创造' }
]

const blankUserForm = () => ({ id: null, username: '', password: '', role: 'STUDENT', realName: '', studentNo: '', employeeNo: '', className: '', department: '', major: '', enabled: true })
const blankQuestionForm = () => ({
  id: null,
  title: '',
  type: 'SINGLE_CHOICE',
  subject: '计算机类',
  courseCode: 'CS-JAVA',
  courseName: 'Java 程序设计',
  chapter: '',
  section: '',
  knowledgeModule: '',
  knowledgePoint: '',
  difficulty: 'EASY',
  cognitiveLevel: 'UNDERSTAND',
  source: '自建题库',
  tags: '',
  options: 'A. 选项1 | B. 选项2',
  correctAnswer: 'A',
  score: 5,
  estimatedMinutes: 3,
  explanation: ''
})
const blankAutoForm = () => ({
  title: '',
  durationMinutes: 90,
  passScore: 60,
  startTime: '',
  endTime: '',
  subject: '',
  courseCode: '',
  courseName: '',
  chapter: '',
  section: '',
  knowledgeModule: '',
  knowledgePoint: '',
  difficulty: '',
  type: '',
  cognitiveLevel: '',
  source: '',
  tags: '',
  questionCount: 5,
  targetClasses: ''
})
const questionFilters = reactive({ subject: '', courseName: '', chapter: '', knowledgeModule: '', type: '', difficulty: '', cognitiveLevel: '', tag: '' })
const admin = reactive({ users: [], classes: [], logs: [], stats: null, userForm: blankUserForm(), classForm: { id: null, name: '', department: '', major: '', studentCount: 0 } })
const teacher = reactive({
  questions: [],
  papers: [],
  analytics: null,
  monitor: null,
  monitorPaperId: null,
  questionForm: blankQuestionForm(),
  manualForm: { title: '', durationMinutes: 90, passScore: 60, startTime: '', endTime: '', questionIds: '', targetClasses: '' },
  autoForm: blankAutoForm(),
  importFile: null
})
const student = reactive({ upcoming: [], history: [], wrongBook: [], stats: { trend: [] } })

const currentExam = ref(null)
const currentQuestions = ref([])
const answers = reactive({})
const currentIndex = ref(0)
const currentSwitchCount = ref(0)
const remainingSeconds = ref(0)
let timer = null
let dialogResolver = null

const dialog = reactive({
  open: false,
  tone: 'default',
  eyebrow: '操作确认',
  title: '',
  message: '',
  details: [],
  confirmText: '确认',
  cancelText: '取消'
})

const isAdmin = computed(() => user.value?.role === 'ADMIN')
const isTeacher = computed(() => user.value?.role === 'TEACHER')
const isStudent = computed(() => user.value?.role === 'STUDENT')

const roleLabel = computed(() => roleName(user.value?.role))
const userInitials = computed(() => (user.value?.realName || user.value?.username || 'U').slice(0, 2).toUpperCase())

const navItems = computed(() => {
  if (isAdmin.value) {
    return [
      { key: 'overview', label: '系统总览', desc: '核心数据快照', code: '01', tab: 'admin', badge: admin.stats?.userCount || 0 },
      { key: 'account-form', label: '账号录入', desc: '细粒度身份维护', code: '02', tab: 'admin' },
      { key: 'users', label: '用户治理', desc: '角色/状态/组织筛选', code: '03', tab: 'admin', badge: filteredUsers.value.length },
      { key: 'classes', label: '班级组织', desc: '院系专业班级', code: '04', tab: 'admin', badge: admin.classes.length },
      { key: 'logs', label: '审计日志', desc: '操作追踪与回溯', code: '05', tab: 'admin', badge: admin.logs.length }
    ]
  }
  if (isTeacher.value) {
    return [
      { key: 'overview', label: '教学总览', desc: '题库/试卷/监考', code: '01', tab: 'teacher' },
      { key: 'questions', label: '题库维护', desc: '题目与知识点', code: '02', tab: 'teacher', badge: teacher.questions.length },
      { key: 'paper-build', label: '智能组卷', desc: '手动/自动组卷', code: '03', tab: 'teacher' },
      { key: 'papers', label: '试卷管理', desc: '发布/监考/导出', code: '04', tab: 'teacher', badge: teacher.papers.length },
      { key: 'monitor', label: '实时监考', desc: '异常与阅卷状态', code: '05', tab: 'teacher', badge: teacher.monitor?.activeCount || 0 },
      { key: 'analytics', label: '成绩分析', desc: '班级与知识点', code: '06', tab: 'teacher' }
    ]
  }
  return [
    { key: 'overview', label: '学习总览', desc: '个人考试状态', code: '01', tab: 'student' },
    { key: 'upcoming', label: '待考考试', desc: '进入正式考试', code: '02', tab: 'student', badge: student.upcoming.length },
    { key: 'transcript', label: '成绩单', desc: '趋势与均分', code: '03', tab: 'student' },
    { key: 'wrong-book', label: '错题本', desc: '薄弱点强化', code: '04', tab: 'student', badge: student.wrongBook.length },
    { key: 'history', label: '历史考试', desc: '答卷回看', code: '05', tab: 'student', badge: student.history.length }
  ]
})

const activeNavItem = computed(() => navItems.value.find(item => item.key === activeNavKey.value) || navItems.value[0] || { label: '工作台', desc: '模块化单页应用' })

const workspaceFeatureCards = computed(() => navItems.value.filter(item => item.key !== 'overview'))

const workspaceSignals = computed(() => {
  if (isAdmin.value) {
    const disabledCount = admin.users.filter(item => item.enabled === false).length
    const missingOrgCount = admin.users.filter(item => !item.department && !item.className).length
    const latestLog = admin.logs[0]
    return [
      {
        label: '权限状态',
        title: disabledCount ? `${disabledCount} 个账号已停用` : '账号权限正常',
        desc: disabledCount ? '可在用户治理中复核冻结账号和离校账号。' : '当前没有冻结账号，登录权限分布稳定。',
        tone: disabledCount ? 'warn' : 'good'
      },
      {
        label: '组织粒度',
        title: missingOrgCount ? `${missingOrgCount} 个账号未分配组织` : '组织信息完整',
        desc: '支持院系、专业、班级、学号/工号、状态多维度维护。',
        tone: missingOrgCount ? 'danger' : 'good'
      },
      {
        label: '最近审计',
        title: latestLog?.action || '暂无操作日志',
        desc: latestLog ? `${latestLog.actorUsername || '系统'} · ${latestLog.detail || latestLog.targetType || '已记录'}` : '关键管理操作会沉淀到审计日志。',
        tone: 'info'
      }
    ]
  }
  if (isTeacher.value) {
    const unpublishedCount = teacher.papers.filter(item => !item.published).length
    const hardCount = teacher.questions.filter(item => item.difficulty === 'HARD').length
    const weakPoint = [...(teacher.analytics?.knowledgePoints || [])].sort((a, b) => (a.correctRate || 0) - (b.correctRate || 0))[0]
    return [
      {
        label: '组卷状态',
        title: unpublishedCount ? `${unpublishedCount} 份试卷待发布` : '试卷发布状态良好',
        desc: unpublishedCount ? '进入试卷管理完成发布、监考和成绩导出。' : '已创建试卷均可进入后续考试流程。',
        tone: unpublishedCount ? 'warn' : 'good'
      },
      {
        label: '题库结构',
        title: `${hardCount} 道高难度题`,
        desc: '题库已按题型、科目、知识点、难度和分值维护。',
        tone: hardCount ? 'info' : 'warn'
      },
      {
        label: '教学反馈',
        title: weakPoint ? `${weakPoint.knowledgePoint || '未分类'} 正确率 ${formatPercent(weakPoint.correctRate)}` : '等待分析数据',
        desc: weakPoint ? '建议围绕低正确率知识点安排讲评。' : '选择试卷后可查看班级、题目和知识点表现。',
        tone: weakPoint && weakPoint.correctRate < 0.45 ? 'danger' : 'info'
      }
    ]
  }
  const weakPoint = [...(student.stats?.knowledgePoints || [])].sort((a, b) => (a.correctRate || 0) - (b.correctRate || 0))[0]
  const latestScore = student.stats?.trend?.[student.stats.trend.length - 1]
  return [
    {
      label: '考试提醒',
      title: student.upcoming.length ? `${student.upcoming.length} 场考试待参加` : '当前没有待考',
      desc: student.upcoming.length ? '进入待考考试模块可直接开始考试。' : '可在历史考试和错题本中继续复盘。',
      tone: student.upcoming.length ? 'warn' : 'good'
    },
    {
      label: '最近成绩',
      title: latestScore ? `${latestScore.label} · ${latestScore.score} 分` : '暂无成绩趋势',
      desc: '成绩单会展示平均分、答卷数、切屏异常和知识点表现。',
      tone: latestScore && latestScore.score < 60 ? 'danger' : 'info'
    },
    {
      label: '薄弱点',
      title: weakPoint ? `${weakPoint.knowledgePoint || '未分类'} ${formatPercent(weakPoint.correctRate)}` : '暂无薄弱点',
      desc: weakPoint ? '建议进入错题本进行针对性练习。' : '完成考试后系统会自动沉淀错题。',
      tone: weakPoint && weakPoint.correctRate < 0.45 ? 'danger' : 'good'
    }
  ]
})

const overviewCards = computed(() => {
  if (isAdmin.value && admin.stats) {
    return [
      { label: '用户总数', value: admin.stats.userCount, tip: '系统账号' },
      { label: '教师账号', value: admin.stats.teacherCount, tip: '授课教师' },
      { label: '学生账号', value: admin.stats.studentCount, tip: '在校学生' },
      { label: '班级数量', value: admin.stats.classCount, tip: '组织结构' },
      { label: '题库数量', value: admin.stats.questionCount, tip: '已维护题目' },
      { label: '试卷数量', value: admin.stats.publishedPaperCount, tip: '已建试卷' }
    ]
  }
  if (isTeacher.value) {
    return [
      { label: '题目数量', value: teacher.questions.length, tip: '题库规模' },
      { label: '试卷数量', value: teacher.papers.length, tip: '组卷成果' },
      { label: '已发布', value: teacher.papers.filter(p => p.published).length, tip: '可考试试卷' },
      { label: '监考中', value: teacher.monitor?.activeCount || 0, tip: '实时答题人数' },
      { label: '待阅卷', value: teacher.monitor?.reviewPendingCount || teacher.analytics?.reviewPendingCount || 0, tip: '主观题答卷' },
      { label: '当前账号', value: user.value?.realName || '-', tip: '教师身份' }
    ]
  }
  return [
    { label: '待考考试', value: student.upcoming.length, tip: '可参加考试' },
    { label: '历史记录', value: student.history.length, tip: '已完成答卷' },
    { label: '错题本', value: student.wrongBook.length, tip: '反复练习' },
    { label: '平均成绩', value: Math.round(student.stats?.averageScore || 0), tip: '个人均分' },
    { label: '可疑答卷', value: student.stats?.suspiciousCount || 0, tip: '切屏异常' },
    { label: '当前账号', value: user.value?.realName || '-', tip: '学生身份' }
  ]
})

const adminDepartmentSegments = computed(() => {
  const map = new Map()
  for (const item of admin.users) {
    const name = item.department || item.className || '未分配'
    map.set(name, (map.get(name) || 0) + 1)
  }
  return Array.from(map, ([name, count]) => ({ name, count })).sort((a, b) => b.count - a.count).slice(0, 4)
})

const adminGovernanceSegments = computed(() => [
  { key: 'all', label: '全部账号', hint: '全量身份台账', role: '', status: '', count: admin.users.length },
  { key: 'admins', label: '管理员', hint: '系统运维权限', role: 'ADMIN', status: '', count: admin.users.filter(item => item.role === 'ADMIN').length },
  { key: 'teachers', label: '教师', hint: '题库与考试业务', role: 'TEACHER', status: '', count: admin.users.filter(item => item.role === 'TEACHER').length },
  { key: 'students', label: '学生', hint: '班级与学号治理', role: 'STUDENT', status: '', count: admin.users.filter(item => item.role === 'STUDENT').length },
  { key: 'disabled', label: '停用账号', hint: '登录权限已冻结', role: '', status: 'disabled', count: admin.users.filter(item => item.enabled === false).length }
])

const filteredUsers = computed(() => admin.users.filter(item => {
  const keywordHit = [item.username, item.realName, item.role, item.studentNo, item.employeeNo, item.className, item.department, item.major].some(value => matches(value, adminUserFilter.value))
  const roleHit = !adminUserRoleFilter.value || item.role === adminUserRoleFilter.value
  const statusHit = !adminUserStatusFilter.value || (adminUserStatusFilter.value === 'enabled' ? item.enabled : !item.enabled)
  return keywordHit && roleHit && statusHit
}))
const filteredClasses = computed(() => admin.classes.filter(item => matches(item.name, adminClassFilter.value) || matches(item.department, adminClassFilter.value) || matches(item.major, adminClassFilter.value)))
const filteredLogs = computed(() => admin.logs.filter(item => matches(item.actorUsername, adminLogFilter.value) || matches(item.action, adminLogFilter.value) || matches(item.targetType, adminLogFilter.value) || matches(item.detail, adminLogFilter.value)))
const questionTagOptions = computed(() => {
  const tags = new Set()
  teacher.questions.forEach(item => normalizeTags(item.tags).forEach(tag => tags.add(tag)))
  return Array.from(tags).sort((a, b) => a.localeCompare(b, 'zh-Hans-CN'))
})
const questionFilterActiveCount = computed(() => Object.values(questionFilters).filter(Boolean).length)
const questionTaxonomyCards = computed(() => [
  { label: '课程覆盖', value: uniqueQuestionValues('courseName').length, tip: '已建课程' },
  { label: '章节覆盖', value: uniqueQuestionValues('chapter').length, tip: '已建章节' },
  { label: '知识模块', value: uniqueQuestionValues('knowledgeModule').length, tip: '可精确定位' },
  { label: '标签数量', value: questionTagOptions.value.length, tip: '题目标签池' }
])
const questionMatrix = computed(() => {
  const map = new Map()
  teacher.questions.forEach(item => {
    const courseName = item.courseName || item.subject || '未归档课程'
    if (!map.has(courseName)) {
      map.set(courseName, { key: courseName, courseName, count: 0, modulesSet: new Set(), hard: 0 })
    }
    const row = map.get(courseName)
    row.count += 1
    if (item.knowledgeModule) row.modulesSet.add(item.knowledgeModule)
    if (item.difficulty === 'HARD') row.hard += 1
  })
  return Array.from(map.values())
    .map(item => ({ ...item, modules: item.modulesSet.size }))
    .sort((a, b) => b.count - a.count)
    .slice(0, 6)
})
const filteredQuestions = computed(() => teacher.questions.filter(item => {
  const keywordFields = [
    item.title,
    item.subject,
    item.courseCode,
    item.courseName,
    item.chapter,
    item.section,
    item.knowledgeModule,
    item.knowledgePoint,
    item.type,
    item.difficulty,
    item.cognitiveLevel,
    item.source,
    normalizeTags(item.tags).join(',')
  ]
  const keywordHit = keywordFields.some(value => matches(value, teacherQuestionFilter.value))
  return keywordHit && questionMatchesFilters(item, questionFilters)
}))
const autoPaperPool = computed(() => teacher.questions.filter(item => questionMatchesAutoForm(item, teacher.autoForm)))
const filteredPapers = computed(() => teacher.papers.filter(item => matches(item.title, teacherPaperFilter.value)))
const filteredMonitorAttempts = computed(() => (teacher.monitor?.attempts || []).filter(item => matches(item.studentUsername, teacherMonitorFilter.value) || matches(item.className, teacherMonitorFilter.value) || matches(item.status, teacherMonitorFilter.value)))
const filteredUpcoming = computed(() => student.upcoming.filter(item => matches(item.title, studentPaperFilter.value)))
const filteredHistory = computed(() => student.history.filter(item => matches(item.paperTitle, studentHistoryFilter.value) || matches(item.status, studentHistoryFilter.value)))
const filteredWrongBook = computed(() => student.wrongBook.filter(item => [item.title, item.subject, item.courseName, item.chapter, item.knowledgeModule, item.knowledgePoint, item.type, normalizeTags(item.tags).join(',')].some(value => matches(value, studentPaperFilter.value))))

const globalSearchResults = computed(() => {
  const keyword = globalSearch.value.trim()
  if (!keyword) return []
  const results = []
  const navByKey = key => navItems.value.find(item => item.key === key) || { key, tab: tab.value }
  const pushResult = (kind, type, title, desc, navKey, payload = {}) => {
    results.push({
      key: `${kind}-${payload.id || payload.attemptId || payload.questionId || results.length}`,
      kind,
      type,
      title: title || '-',
      desc: desc || '',
      nav: navByKey(navKey),
      payload
    })
  }

  if (isAdmin.value) {
    admin.users
      .filter(item => [item.username, item.realName, item.role, item.studentNo, item.employeeNo, item.className, item.department, item.major].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('admin-user', '用户', item.realName || item.username, `${roleName(item.role)} · ${formatOrg(item)}`, 'users', item))
    admin.classes
      .filter(item => [item.name, item.department, item.major].some(value => matches(value, keyword)))
      .slice(0, 3)
      .forEach(item => pushResult('admin-class', '班级', item.name, `${item.department || '未设置院系'} · ${item.major || '未设置专业'}`, 'classes', item))
    admin.logs
      .filter(item => [item.actorUsername, item.action, item.targetType, item.detail].some(value => matches(value, keyword)))
      .slice(0, 3)
      .forEach(item => pushResult('admin-log', '日志', item.action, `${item.actorUsername || '系统'} · ${item.detail || item.targetType || '-'}`, 'logs', item))
  }

  if (isTeacher.value) {
    teacher.questions
      .filter(item => [item.title, item.subject, item.courseCode, item.courseName, item.chapter, item.section, item.knowledgeModule, item.knowledgePoint, item.type, item.difficulty, item.cognitiveLevel, item.source, normalizeTags(item.tags).join(',')].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('teacher-question', '题目', item.title, `${questionPath(item)} · ${item.knowledgePoint || item.knowledgeModule || '未分类'} · ${item.score || 0} 分`, 'questions', item))
    teacher.papers
      .filter(item => [item.title, item.totalScore, item.durationMinutes, item.published ? '已发布' : '未发布'].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('teacher-paper', '试卷', item.title, `${item.published ? '已发布' : '未发布'} · ${item.durationMinutes || 0} 分钟 · ${item.totalScore || 0} 分`, 'papers', item))
    ;(teacher.monitor?.attempts || [])
      .filter(item => [item.studentUsername, item.className, item.status].some(value => matches(value, keyword)))
      .slice(0, 3)
      .forEach(item => pushResult('teacher-monitor', '监考', item.studentUsername, `${item.className || '未分班'} · ${item.status}`, 'monitor', item))
  }

  if (isStudent.value) {
    student.upcoming
      .filter(item => [item.title, item.durationMinutes, item.totalScore].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('student-upcoming', '待考', item.title, `${item.durationMinutes || 0} 分钟 · ${item.totalScore || 0} 分`, 'upcoming', item))
    student.history
      .filter(item => [item.paperTitle, item.status, item.score].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('student-history', '历史', item.paperTitle, `${item.status} · ${item.score || 0} 分`, 'history', item))
    student.wrongBook
      .filter(item => [item.title, item.subject, item.courseName, item.chapter, item.knowledgeModule, item.knowledgePoint, item.type, normalizeTags(item.tags).join(',')].some(value => matches(value, keyword)))
      .slice(0, 4)
      .forEach(item => pushResult('student-wrong', '错题', item.title, `${item.subject || '综合'} · 错 ${item.wrongCount || 0} 次`, 'wrong-book', item))
  }

  return results.slice(0, 8)
})

const paginatedUsers = computed(() => slicePage(filteredUsers.value, userPage.value))
const paginatedClasses = computed(() => slicePage(filteredClasses.value, classPage.value))
const paginatedLogs = computed(() => slicePage(filteredLogs.value, logPage.value))
const paginatedQuestions = computed(() => slicePage(filteredQuestions.value, questionListPage.value, 8))
const paginatedPapers = computed(() => slicePage(filteredPapers.value, paperListPage.value, 8))
const paginatedHistory = computed(() => slicePage(filteredHistory.value, historyPage.value))
const allUsersOnPageSelected = computed(() => paginatedUsers.value.length > 0 && paginatedUsers.value.every(item => selectedUserIds.value.includes(item.id)))
const allClassesOnPageSelected = computed(() => paginatedClasses.value.length > 0 && paginatedClasses.value.every(item => selectedClassIds.value.includes(item.id)))
const allQuestionsOnPageSelected = computed(() => paginatedQuestions.value.length > 0 && paginatedQuestions.value.every(item => selectedQuestionIds.value.includes(item.id)))
const allPapersOnPageSelected = computed(() => paginatedPapers.value.length > 0 && paginatedPapers.value.every(item => selectedPaperIds.value.includes(item.id)))

const formattedRemaining = computed(() => {
  const m = String(Math.floor(remainingSeconds.value / 60)).padStart(2, '0')
  const s = String(remainingSeconds.value % 60).padStart(2, '0')
  return `${m}:${s}`
})

const examTimerPercent = computed(() => {
  if (!currentExam.value) return 100
  const total = (currentExam.value.durationMinutes || 0) * 60
  if (total === 0) return 100
  return Math.max(0, (remainingSeconds.value / total) * 100)
})

function slicePage(list, page, size = pageSize) {
  const start = (page - 1) * size
  return list.slice(start, start + size)
}

function toggleSelection(store, id) {
  if (store.value.includes(id)) {
    store.value = store.value.filter(item => item !== id)
    return
  }
  store.value = [...store.value, id]
}

function togglePageSelection(store, list, checked) {
  const ids = list.map(item => item.id)
  if (checked) {
    store.value = Array.from(new Set([...store.value, ...ids]))
    return
  }
  store.value = store.value.filter(id => !ids.includes(id))
}

function toggleUserSelection(id) { toggleSelection(selectedUserIds, id) }
function toggleClassSelection(id) { toggleSelection(selectedClassIds, id) }
function toggleQuestionSelection(id) { toggleSelection(selectedQuestionIds, id) }
function togglePaperSelection(id) { toggleSelection(selectedPaperIds, id) }
function toggleUserPageSelection(checked) { togglePageSelection(selectedUserIds, paginatedUsers.value, checked) }
function toggleClassPageSelection(checked) { togglePageSelection(selectedClassIds, paginatedClasses.value, checked) }
function toggleQuestionPageSelection(checked) { togglePageSelection(selectedQuestionIds, paginatedQuestions.value, checked) }
function togglePaperPageSelection(checked) { togglePageSelection(selectedPaperIds, paginatedPapers.value, checked) }

function matches(value, keyword) {
  if (!keyword) return true
  return String(value || '').toLowerCase().includes(String(keyword).toLowerCase())
}

function sameValue(expected, actual) {
  return !expected || String(expected).trim().toLowerCase() === String(actual || '').trim().toLowerCase()
}

function normalizeTags(value) {
  if (Array.isArray(value)) return value.map(item => String(item || '').trim()).filter(Boolean)
  return parseTags(value)
}

function uniqueQuestionValues(field) {
  const values = new Set()
  teacher.questions.forEach(item => {
    const value = item?.[field]
    if (value !== null && value !== undefined && String(value).trim()) values.add(String(value).trim())
  })
  return Array.from(values).sort((a, b) => a.localeCompare(b, 'zh-Hans-CN'))
}

function questionMatchesFilters(question, filters) {
  const tagHit = !filters.tag || normalizeTags(question.tags).some(tag => sameValue(filters.tag, tag))
  return sameValue(filters.subject, question.subject)
    && sameValue(filters.courseName, question.courseName)
    && sameValue(filters.chapter, question.chapter)
    && sameValue(filters.knowledgeModule, question.knowledgeModule)
    && sameValue(filters.type, question.type)
    && sameValue(filters.difficulty, question.difficulty)
    && sameValue(filters.cognitiveLevel, question.cognitiveLevel)
    && tagHit
}

function questionMatchesAutoForm(question, form) {
  const expectedTags = normalizeTags(form.tags)
  const actualTags = normalizeTags(question.tags).map(tag => tag.toLowerCase())
  const tagHit = expectedTags.every(tag => actualTags.includes(tag.toLowerCase()))
  return sameValue(form.subject, question.subject)
    && sameValue(form.courseCode, question.courseCode)
    && sameValue(form.courseName, question.courseName)
    && sameValue(form.chapter, question.chapter)
    && sameValue(form.section, question.section)
    && sameValue(form.knowledgeModule, question.knowledgeModule)
    && sameValue(form.knowledgePoint, question.knowledgePoint)
    && sameValue(form.difficulty, question.difficulty)
    && sameValue(form.type, question.type)
    && sameValue(form.cognitiveLevel, question.cognitiveLevel)
    && sameValue(form.source, question.source)
    && tagHit
}

function questionPath(question) {
  return [question.courseName || question.subject || '未归档课程', question.chapter, question.section].filter(Boolean).join(' / ')
}

function questionIdentity(question) {
  return [question.subject || '综合', question.source || '未设置来源', question.cognitiveLevel || '未设能力层级'].filter(Boolean).join(' · ')
}

function resetQuestionFilters() {
  Object.assign(questionFilters, { subject: '', courseName: '', chapter: '', knowledgeModule: '', type: '', difficulty: '', cognitiveLevel: '', tag: '' })
  teacherQuestionFilter.value = ''
  questionListPage.value = 1
}

function roleName(role) {
  return ({ ADMIN: '管理员', TEACHER: '教师', STUDENT: '学生' })[role] || '访客'
}

function roleBadge(role) {
  if (role === 'ADMIN') return 'purple'
  if (role === 'TEACHER') return 'blue'
  if (role === 'STUDENT') return 'green'
  return 'cyan'
}

function formatOrg(row) {
  return [row.department, row.major, row.className].filter(Boolean).join(' / ') || '未分配'
}

function resetUserForm() {
  admin.userForm = blankUserForm()
}

function setAdminSegment(item) {
  adminUserRoleFilter.value = item.role
  adminUserStatusFilter.value = item.status
  userPage.value = 1
}

function openDialog(config = {}) {
  Object.assign(dialog, {
    open: true,
    tone: config.tone || 'default',
    eyebrow: config.eyebrow || '操作确认',
    title: config.title || '确认继续操作？',
    message: config.message || '该操作将立即生效。',
    details: config.details || [],
    confirmText: config.confirmText || '确认',
    cancelText: config.cancelText || '取消'
  })
  return new Promise(resolve => {
    dialogResolver = resolve
  })
}

function confirmDialog() {
  dialog.open = false
  dialogResolver?.(true)
  dialogResolver = null
}

function cancelDialog() {
  dialog.open = false
  dialogResolver?.(false)
  dialogResolver = null
}

async function goSection(item) {
  if (!item) return
  activeNavKey.value = item.key
  tab.value = item.tab
  if (item.tab === 'admin') await loadAdmin()
  if (item.tab === 'teacher' && teacher.questions.length === 0 && teacher.papers.length === 0) await loadTeacher()
  if (item.tab === 'student' && student.upcoming.length === 0 && student.history.length === 0) await loadStudent()
  await nextTick()
  sidebarOpen.value = false
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function openGlobalResult(item) {
  if (!item) return
  await goSection(item.nav)
  if (item.kind === 'admin-user') {
    adminUserFilter.value = item.payload.username || item.payload.realName || globalSearch.value
    userPage.value = 1
  }
  if (item.kind === 'admin-class') {
    adminClassFilter.value = item.payload.name || globalSearch.value
    classPage.value = 1
  }
  if (item.kind === 'admin-log') {
    adminLogFilter.value = item.payload.action || globalSearch.value
    logPage.value = 1
  }
  if (item.kind === 'teacher-question') {
    teacherQuestionFilter.value = item.payload.title || globalSearch.value
    questionListPage.value = 1
  }
  if (item.kind === 'teacher-paper') {
    teacherPaperFilter.value = item.payload.title || globalSearch.value
    paperListPage.value = 1
  }
  if (item.kind === 'teacher-monitor' && item.payload.attemptId) {
    teacherMonitorFilter.value = item.payload.studentUsername || globalSearch.value
  }
  if (item.kind === 'student-upcoming') {
    studentPaperFilter.value = item.payload.title || globalSearch.value
  }
  if (item.kind === 'student-history') {
    studentHistoryFilter.value = item.payload.paperTitle || globalSearch.value
    historyPage.value = 1
  }
  if (item.kind === 'student-wrong') {
    studentPaperFilter.value = item.payload.title || globalSearch.value
  }
  globalSearch.value = ''
}

function setMessage(text, type = 'notice') {
  message.value = text
  messageType.value = type
  if (text) setTimeout(() => (message.value = ''), 4000)
}

function addNotif(text) {
  const now = new Date()
  const time = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
  notifications.value.unshift({ text, time })
  if (notifications.value.length > 50) notifications.value.pop()
}

async function request(path, options = {}) {
  const headers = options.headers || {}
  const init = { method: options.method || 'GET', headers }
  if (options.body && !(options.body instanceof FormData)) {
    headers['Content-Type'] = 'application/json'
    init.body = JSON.stringify(options.body)
  } else if (options.body instanceof FormData) {
    init.body = options.body
  }
  let res
  try {
    res = await fetch(path, init)
  } catch {
    throw new Error('后端服务未启动或无法访问')
  }
  const text = await res.text()
  if (!text) {
    if (res.ok) return null
    throw new Error(`请求失败：${res.status} ${res.statusText}`)
  }
  let data
  try { data = JSON.parse(text) } catch {
    throw new Error(`接口返回非 JSON：${res.status} ${res.statusText}`)
  }
  if (!res.ok || data.success === false) {
    throw new Error(data.message || `请求失败：${res.status} ${res.statusText}`)
  }
  return data.data
}

async function login() {
  loggingIn.value = true
  try {
    const data = await request('/api/auth/login', { method: 'POST', body: loginForm })
    user.value = data
    localStorage.setItem('exam-user', JSON.stringify(data))
    tab.value = data.role === 'ADMIN' ? 'admin' : data.role === 'TEACHER' ? 'teacher' : 'student'
    await loadAll()
    setMessage('登录成功')
    addNotif(`欢迎回来，${data.realName || data.username}`)
  } catch (e) { setMessage(e.message, 'error') }
  finally { loggingIn.value = false }
}

function fillLogin(username, password) {
  loginForm.username = username
  loginForm.password = password
}

function logout() {
  closeExam()
  openProfile.value = false
  paperDetail.value = null
  attemptDetail.value = null
  showNotif.value = false
  user.value = null
  localStorage.removeItem('exam-user')
}

async function loadAll() {
  if (isAdmin.value) await loadAdmin()
  if (isTeacher.value) await loadTeacher()
  if (isStudent.value) await loadStudent()
}

async function loadAdmin() {
  adminLoading.value = true
  try {
    admin.users = await request('/api/admin/users')
    admin.classes = await request('/api/admin/classes')
    admin.logs = await request('/api/admin/logs')
    admin.stats = await request('/api/admin/stats')
    selectedUserIds.value = []
    selectedClassIds.value = []
  } catch (e) { setMessage(e.message, 'error') }
  finally { adminLoading.value = false }
}

async function saveUser() {
  try {
    const payload = { ...admin.userForm }
    const method = admin.userForm.id ? 'PUT' : 'POST'
    const url = admin.userForm.id ? `/api/admin/users/${admin.userForm.id}?actor=${encodeURIComponent(user.value.username)}` : `/api/admin/users?actor=${encodeURIComponent(user.value.username)}`
    await request(url, { method, body: payload })
    admin.userForm = blankUserForm()
    await loadAdmin()
    setMessage('用户已保存')
    addNotif('用户数据已更新')
  } catch (e) { setMessage(e.message, 'error') }
}

function toQuestionPayload(row, overrides = {}) {
  return {
    ...row,
    ...overrides,
    tags: normalizeTags(row.tags),
    options: Array.isArray(row.options) ? row.options.map(item => String(item || '').trim()).filter(Boolean) : String(row.options || '').split('|').map(s => s.trim()).filter(Boolean),
    score: Number(row.score || 0),
    estimatedMinutes: row.estimatedMinutes ? Number(row.estimatedMinutes) : null,
    creatorUsername: row.creatorUsername || user.value.username
  }
}

function editUser(row) {
  activeNavKey.value = 'account-form'
  admin.userForm = { ...blankUserForm(), ...row, password: '', enabled: row.enabled !== false }
  scrollToForm('userFormCard')
}

async function toggleUserEnabled(row) {
  const action = row.enabled ? '停用' : '启用'
  const ok = await openDialog({
    tone: row.enabled ? 'danger' : 'default',
    eyebrow: '账号权限',
    title: `${action}账号？`,
    message: `将${action} ${row.realName || row.username} 的登录权限。`,
    details: [row.username, roleName(row.role), formatOrg(row)].filter(Boolean),
    confirmText: action
  })
  if (!ok) return
  try {
    const payload = { ...blankUserForm(), ...row, password: '', enabled: !row.enabled }
    await request(`/api/admin/users/${row.id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'PUT', body: payload })
    await loadAdmin()
    setMessage(`账号已${action}`)
  } catch (e) { setMessage(e.message, 'error') }
}

async function deleteUser(id) {
  const target = admin.users.find(item => item.id === id)
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '危险操作',
    title: '删除用户？',
    message: `将永久删除账号 ${target?.username || `#${id}`}，该操作不可撤销。`,
    details: [target?.realName, target?.role && roleName(target.role), target && formatOrg(target)].filter(Boolean),
    confirmText: '删除'
  })
  if (!ok) return
  try {
    await request(`/api/admin/users/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    await loadAdmin()
    setMessage('用户已删除')
  } catch (e) { setMessage(e.message, 'error') }
}

async function deleteSelectedUsers() {
  const ids = selectedUserIds.value
  if (!ids.length) return
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '危险操作',
    title: '批量删除用户？',
    message: `将删除 ${ids.length} 个用户账号，删除后不可恢复。`,
    confirmText: '批量删除'
  })
  if (!ok) return
  let failed = 0
  for (const id of ids) {
    try {
      await request(`/api/admin/users/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    } catch {
      failed += 1
    }
  }
  await loadAdmin()
  if (failed > 0) {
    setMessage(`已删除 ${ids.length - failed} 个用户，${failed} 个删除失败`, 'error')
    return
  }
  setMessage(`已删除 ${ids.length} 个用户`)
}

async function saveClass() {
  try {
    const method = admin.classForm.id ? 'PUT' : 'POST'
    const url = admin.classForm.id ? `/api/admin/classes/${admin.classForm.id}?actor=${encodeURIComponent(user.value.username)}` : `/api/admin/classes?actor=${encodeURIComponent(user.value.username)}`
    await request(url, { method, body: admin.classForm })
    admin.classForm = { id: null, name: '', department: '', major: '', studentCount: 0 }
    await loadAdmin()
    setMessage('班级已保存')
  } catch (e) { setMessage(e.message, 'error') }
}

function editClass(row) {
  activeNavKey.value = 'classes'
  admin.classForm = { ...row }
  scrollToForm('classFormCard')
}

function scrollToForm(refName) {
  nextTick(() => {
    const el = document.getElementById(refName)
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  })
}

async function deleteClass(id) {
  const target = admin.classes.find(item => item.id === id)
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '组织架构',
    title: '删除班级？',
    message: `将删除班级 ${target?.name || `#${id}`}，相关账号不会自动迁移。`,
    details: [target?.department, target?.major, `${target?.studentCount || 0} 人`].filter(Boolean),
    confirmText: '删除'
  })
  if (!ok) return
  try {
    await request(`/api/admin/classes/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    await loadAdmin()
    setMessage('班级已删除')
  } catch (e) { setMessage(e.message, 'error') }
}

async function deleteSelectedClasses() {
  const ids = selectedClassIds.value
  if (!ids.length) return
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '组织架构',
    title: '批量删除班级？',
    message: `将删除 ${ids.length} 个班级，相关账号不会自动迁移。`,
    confirmText: '批量删除'
  })
  if (!ok) return
  let failed = 0
  for (const id of ids) {
    try {
      await request(`/api/admin/classes/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    } catch {
      failed += 1
    }
  }
  await loadAdmin()
  if (failed > 0) {
    setMessage(`已删除 ${ids.length - failed} 个班级，${failed} 个删除失败`, 'error')
    return
  }
  setMessage(`已删除 ${ids.length} 个班级`)
}

async function loadTeacher() {
  teacher.questions = await request('/api/teacher/questions')
  teacher.papers = await request('/api/teacher/papers')
  selectedQuestionIds.value = []
  selectedPaperIds.value = []
  if (teacher.monitorPaperId) await loadMonitor(teacher.monitorPaperId, false, false)
}

function parseTags(value) {
  if (Array.isArray(value)) return value.map(item => String(item || '').trim()).filter(Boolean)
  return value ? String(value).split(/[,，]/).map(s => s.trim()).filter(Boolean) : []
}
function parseTargetClasses(value) {
  const classes = parseTags(value)
  return classes.some(isAllClassesToken) ? [] : classes
}
function isAllClassesToken(value) {
  return ['*', 'all', '全部', '全部班级', '所有班级'].includes(String(value || '').replace(/\s+/g, '').toLowerCase())
}
function toDateTimePayload(value) { return value || null }
function formatPercent(value) { return `${Math.round((value || 0) * 100)}%` }
function formatSeconds(value) {
  const total = Math.max(0, Number(value || 0))
  const h = Math.floor(total / 3600)
  const m = Math.floor((total % 3600) / 60)
  const s = total % 60
  return h > 0 ? `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}` : `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}
function statusClass(status) {
  if (status === 'SUBMITTED' || status === 'REVIEWED') return 'green'
  if (status === 'REVIEW_PENDING') return 'amber'
  if (status === 'AUTO_SUBMITTED') return 'red'
  return 'cyan'
}
function parsePaperTime(value) { return value ? new Date(value).getTime() : null }
function isBeforePaperStart(paper) {
  const start = parsePaperTime(paper?.startTime)
  return start !== null && Date.now() < start
}
function isAfterPaperEnd(paper) {
  const end = parsePaperTime(paper?.endTime)
  return end !== null && Date.now() > end
}
function isPaperOpen(paper) { return !isBeforePaperStart(paper) && !isAfterPaperEnd(paper) }
function paperStatusLabel(paper) {
  if (isBeforePaperStart(paper)) return '未开始'
  if (isAfterPaperEnd(paper)) return '已结束'
  return '进行中'
}
function paperActionLabel(paper) {
  if (isBeforePaperStart(paper)) return '未到开始时间'
  if (isAfterPaperEnd(paper)) return '考试已结束'
  return '开始考试'
}
function formatPaperTime(value) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : '不限'
}
function paperTimeRange(paper) {
  return `${formatPaperTime(paper?.startTime)} - ${formatPaperTime(paper?.endTime)}`
}

async function saveQuestion() {
  try {
    const payload = toQuestionPayload(teacher.questionForm)
    const method = teacher.questionForm.id ? 'PUT' : 'POST'
    const url = teacher.questionForm.id ? `/api/teacher/questions/${teacher.questionForm.id}?actor=${encodeURIComponent(user.value.username)}` : `/api/teacher/questions?actor=${encodeURIComponent(user.value.username)}`
    await request(url, { method, body: payload })
    teacher.questionForm = blankQuestionForm()
    await loadTeacher()
    setMessage('题目已保存')
  } catch (e) { setMessage(e.message, 'error') }
}

function editQuestion(row) {
  activeNavKey.value = 'questions'
  teacher.questionForm = {
    ...blankQuestionForm(),
    ...row,
    options: Array.isArray(row.options) ? row.options.join(' | ') : String(row.options || ''),
    tags: normalizeTags(row.tags).join(', ')
  }
  scrollToForm('questionFormCard')
}

async function deleteQuestion(id) {
  const target = teacher.questions.find(item => item.id === id)
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '题库维护',
    title: '删除题目？',
    message: `将删除题目 ${target?.title || `#${id}`}。`,
    details: [target?.subject, target?.knowledgePoint, target?.type].filter(Boolean),
    confirmText: '删除'
  })
  if (!ok) return
  try {
    await request(`/api/teacher/questions/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    await loadTeacher()
    setMessage('题目已删除')
  } catch (e) { setMessage(e.message, 'error') }
}

async function deleteSelectedQuestions() {
  const ids = selectedQuestionIds.value
  if (!ids.length) return
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '题库维护',
    title: '批量删除题目？',
    message: `将删除 ${ids.length} 道题目。`,
    confirmText: '批量删除'
  })
  if (!ok) return
  let failed = 0
  for (const id of ids) {
    try {
      await request(`/api/teacher/questions/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    } catch {
      failed += 1
    }
  }
  await loadTeacher()
  if (failed > 0) {
    setMessage(`已删除 ${ids.length - failed} 道题目，${failed} 个删除失败`, 'error')
    return
  }
  setMessage(`已删除 ${ids.length} 道题目`)
}

async function updateSelectedQuestionDifficulty() {
  if (!batchQuestionDifficulty.value) return setMessage('请选择目标难度', 'error')
  await updateSelectedQuestions({ difficulty: batchQuestionDifficulty.value }, `难度：${batchQuestionDifficulty.value}`)
  batchQuestionDifficulty.value = ''
}

async function updateSelectedQuestionType() {
  if (!batchQuestionType.value) return setMessage('请选择目标题型', 'error')
  await updateSelectedQuestions({ type: batchQuestionType.value }, `题型：${batchQuestionType.value}`)
  batchQuestionType.value = ''
}

async function updateSelectedQuestions(overrides, actionLabel) {
  const ids = [...selectedQuestionIds.value]
  if (!ids.length) return
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '题库维护',
    title: '批量修改题目？',
    message: `将修改 ${ids.length} 道题目（${actionLabel}）。`,
    confirmText: '批量修改'
  })
  if (!ok) return
  let failed = 0
  for (const id of ids) {
    const target = teacher.questions.find(item => item.id === id)
    if (!target) {
      failed += 1
      continue
    }
    try {
      await request(`/api/teacher/questions/${id}?actor=${encodeURIComponent(user.value.username)}`, {
        method: 'PUT',
        body: toQuestionPayload(target, overrides)
      })
    } catch {
      failed += 1
    }
  }
  await loadTeacher()
  if (failed > 0) {
    setMessage(`已修改 ${ids.length - failed} 道题目，${failed} 个修改失败`, 'error')
    return
  }
  setMessage(`已批量修改 ${ids.length} 道题目`)
}

async function openPaperDetail(id) {
  const meta = await request(`/api/teacher/papers/${id}`)
  const items = await request(`/api/teacher/papers/${id}/items`)
  paperDetail.value = { meta, questions: items.map(item => JSON.parse(item.questionSnapshotJson)) }
}

function onImportFile(event) { teacher.importFile = event.target.files?.[0] || null }

async function importQuestions() {
  if (!teacher.importFile) return setMessage('请选择 Excel 文件', 'error')
  try {
    const form = new FormData()
    form.append('file', teacher.importFile)
    await request(`/api/teacher/questions/import?actor=${encodeURIComponent(user.value.username)}`, { method: 'POST', body: form })
    teacher.importFile = null
    await loadTeacher()
    setMessage('导入完成')
    addNotif('题目导入成功')
  } catch (e) { setMessage(e.message, 'error') }
}

function downloadQuestionTemplate() { window.open('/api/teacher/questions/template', '_blank') }

async function createManualPaper() {
  try {
    const payload = {
      title: teacher.manualForm.title,
      durationMinutes: teacher.manualForm.durationMinutes,
      passScore: teacher.manualForm.passScore,
      startTime: toDateTimePayload(teacher.manualForm.startTime),
      endTime: toDateTimePayload(teacher.manualForm.endTime),
      questionIds: teacher.manualForm.questionIds.split(',').map(s => Number(s.trim())).filter(Boolean),
      targetClasses: parseTargetClasses(teacher.manualForm.targetClasses)
    }
    await request(`/api/teacher/papers/manual?actor=${encodeURIComponent(user.value.username)}`, { method: 'POST', body: payload })
    await loadTeacher()
    activeNavKey.value = 'papers'
    setMessage('手动组卷成功')
    addNotif(`试卷"${payload.title}"已生成`)
  } catch (e) { setMessage(e.message, 'error') }
}

async function createAutoPaper() {
  try {
    const payload = {
      title: teacher.autoForm.title,
      durationMinutes: teacher.autoForm.durationMinutes,
      passScore: teacher.autoForm.passScore,
      startTime: toDateTimePayload(teacher.autoForm.startTime),
      endTime: toDateTimePayload(teacher.autoForm.endTime),
      subject: teacher.autoForm.subject,
      courseCode: teacher.autoForm.courseCode,
      courseName: teacher.autoForm.courseName,
      chapter: teacher.autoForm.chapter,
      section: teacher.autoForm.section,
      knowledgeModule: teacher.autoForm.knowledgeModule,
      knowledgePoint: teacher.autoForm.knowledgePoint,
      difficulty: teacher.autoForm.difficulty,
      type: teacher.autoForm.type,
      cognitiveLevel: teacher.autoForm.cognitiveLevel,
      source: teacher.autoForm.source,
      tags: normalizeTags(teacher.autoForm.tags),
      questionCount: teacher.autoForm.questionCount,
      targetClasses: parseTargetClasses(teacher.autoForm.targetClasses)
    }
    await request(`/api/teacher/papers/auto?actor=${encodeURIComponent(user.value.username)}`, { method: 'POST', body: payload })
    await loadTeacher()
    activeNavKey.value = 'papers'
    setMessage('自动组卷成功')
    addNotif(`自动试卷"${payload.title}"已生成`)
  } catch (e) { setMessage(e.message, 'error') }
}

async function publishPaper(id) {
  try {
    const paper = teacher.papers.find(item => item.id === id)
    await request(`/api/teacher/papers/${id}/publish?actor=${encodeURIComponent(user.value.username)}`, { method: 'POST', body: { targetClasses: paper?.targetClasses || [] } })
    await loadTeacher()
    setMessage('试卷已发布')
    addNotif('试卷已发布')
  } catch (e) { setMessage(e.message, 'error') }
}

async function deletePaper(id) {
  const target = teacher.papers.find(item => item.id === id)
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '试卷管理',
    title: '删除试卷？',
    message: `将删除试卷 ${target?.title || `#${id}`}，相关考试数据也会被清理。`,
    details: [`总分 ${target?.totalScore || 0}`, `${target?.durationMinutes || 0} 分钟`, target?.published ? '已发布' : '未发布'],
    confirmText: '删除'
  })
  if (!ok) return
  try {
    await request(`/api/teacher/papers/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    await loadTeacher()
    setMessage('试卷已删除')
    addNotif('试卷已删除')
  } catch (e) { setMessage(e.message, 'error') }
}

async function deleteSelectedPapers() {
  const ids = selectedPaperIds.value
  if (!ids.length) return
  const ok = await openDialog({
    tone: 'danger',
    eyebrow: '试卷管理',
    title: '批量删除试卷？',
    message: `将删除 ${ids.length} 份试卷，相关考试数据也会被清理。`,
    confirmText: '批量删除'
  })
  if (!ok) return
  let failed = 0
  for (const id of ids) {
    try {
      await request(`/api/teacher/papers/${id}?actor=${encodeURIComponent(user.value.username)}`, { method: 'DELETE' })
    } catch {
      failed += 1
    }
  }
  await loadTeacher()
  if (failed > 0) {
    setMessage(`已删除 ${ids.length - failed} 份试卷，${failed} 个删除失败`, 'error')
    return
  }
  setMessage(`已删除 ${ids.length} 份试卷`)
  addNotif(`已批量删除 ${ids.length} 份试卷`)
}

async function loadAnalytics(id, navigate = true) {
  try {
    if (navigate) activeNavKey.value = 'analytics'
    teacher.analytics = await request(`/api/teacher/exams/${id}/analytics`)
  } catch (e) { setMessage(e.message, 'error') }
}

async function loadMonitor(id, notify = true, navigate = true) {
  try {
    if (navigate) activeNavKey.value = 'monitor'
    teacher.monitorPaperId = id
    teacher.monitor = await request(`/api/teacher/exams/${id}/monitor`)
    if (notify) setMessage('监考数据已刷新')
  } catch (e) { setMessage(e.message, 'error') }
}

async function extendAttempt(attemptId, minutes = 10) {
  try {
    await request(`/api/teacher/attempts/${attemptId}/extend?actor=${encodeURIComponent(user.value.username)}`, { method: 'POST', body: { minutes } })
    if (teacher.monitorPaperId) await loadMonitor(teacher.monitorPaperId, false, false)
    setMessage(`已延长 ${minutes} 分钟`)
  } catch (e) { setMessage(e.message, 'error') }
}

async function reviewAnswer(answer) {
  if (!attemptDetail.value) return
  try {
    attemptDetail.value = await request(`/api/teacher/attempts/${attemptDetail.value.attemptId}/review?actor=${encodeURIComponent(user.value.username)}`, {
      method: 'POST',
      body: { questionId: answer.questionId, score: Number(answer.draftScore || 0), correct: Number(answer.draftScore || 0) > 0 }
    })
    hydrateAttemptDrafts()
    if (teacher.monitorPaperId) await loadMonitor(teacher.monitorPaperId, false, false)
    if (teacher.analytics) await loadAnalytics(attemptDetail.value.paperId, false)
    setMessage('阅卷已保存')
  } catch (e) { setMessage(e.message, 'error') }
}

function exportScores(id) { window.open(`/api/teacher/exams/${id}/export`, '_blank') }

async function loadStudent() {
  if (!user.value) return
  student.upcoming = await request(`/api/student/exams?username=${encodeURIComponent(user.value.username)}`)
  student.history = await request(`/api/student/history?username=${encodeURIComponent(user.value.username)}`)
  student.wrongBook = await request(`/api/student/wrong-book?username=${encodeURIComponent(user.value.username)}`)
  student.stats = await request(`/api/student/stats?username=${encodeURIComponent(user.value.username)}`)
}

async function changePassword() {
  try {
    await request('/api/auth/password', {
      method: 'PUT',
      body: { username: user.value.username, oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword }
    })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    openProfile.value = false
    setMessage('密码已修改')
  } catch (e) { setMessage(e.message, 'error') }
}

async function openAttemptDetail(attemptId) {
  const prefix = isTeacher.value ? '/api/teacher' : '/api/student'
  attemptDetail.value = await request(`${prefix}/attempts/${attemptId}`)
  hydrateAttemptDrafts()
}

function hydrateAttemptDrafts() {
  if (!attemptDetail.value?.answers) return
  attemptDetail.value.answers = attemptDetail.value.answers.map(answer => ({
    ...answer,
    draftScore: answer.score || 0
  }))
}

async function startExam(paperId) {
  try {
    const data = await request(`/api/student/exams/${paperId}/start?username=${encodeURIComponent(user.value.username)}`, { method: 'POST' })
    currentExam.value = data
    currentQuestions.value = data.questions || []
    currentIndex.value = 0
    currentSwitchCount.value = 0
    remainingSeconds.value = data.remainingSeconds || (data.durationMinutes || 0) * 60
    Object.keys(answers).forEach(key => delete answers[key])
    for (const q of currentQuestions.value) answers[q.id] = ''
    startTimer()
    window.addEventListener('blur', onBlur)
    setMessage('已进入考试')
    addNotif(`已开始考试：${data.paperTitle}`)
  } catch (e) { setMessage(e.message, 'error') }
}

function onBlur() { if (currentExam.value) recordSwitch() }

async function recordSwitch() {
  if (!currentExam.value) return
  try {
    const data = await request(`/api/student/attempts/${currentExam.value.attemptId}/switch`, { method: 'POST' })
    currentSwitchCount.value = data.switchCount
    if (data.status !== 'IN_PROGRESS') {
      setMessage('因切屏超限已自动交卷', 'error')
      await finishAttempt(data)
    }
  } catch (e) { setMessage(e.message, 'error') }
}

function startTimer() {
  clearInterval(timer)
  timer = setInterval(async () => {
    if (!currentExam.value) return
    remainingSeconds.value -= 1
    if (remainingSeconds.value <= 0) {
      clearInterval(timer)
      await submitExam()
    }
  }, 1000)
}

function needTextArea(type) { return ['SHORT_ANSWER', 'CODING'].includes(type) }
function needsManualReview(answer) { return ['SHORT_ANSWER', 'CODING'].includes(answer?.type) || answer?.reviewed === false }
function isChoiceQuestion(type) { return ['SINGLE_CHOICE', 'MULTI_CHOICE', 'TRUE_FALSE'].includes(type) }

function optionKey(option, index = 0) {
  const text = String(option || '').trim()
  const match = text.match(/^([A-Z])[\.\．、\)\）:\s]+/i)
  return (match?.[1] || String.fromCharCode(65 + index)).toUpperCase()
}

function optionText(option, index = 0) {
  const text = String(option || '').trim()
  const prefix = optionKey(option, index)
  return text.replace(new RegExp(`^${prefix}[\\.\\．、\\)\\）:\\s]+`, 'i'), '').trim() || text
}

function selectedKeys(value) {
  return String(value || '')
    .split(/[,，、;；|\s]+/)
    .map(item => item.trim().toUpperCase())
    .filter(Boolean)
}

function sortAnswerKeys(keys) {
  return Array.from(new Set(keys)).sort((a, b) => a.charCodeAt(0) - b.charCodeAt(0)).join(',')
}

function isOptionSelected(questionId, option, index) {
  return selectedKeys(answers[questionId]).includes(optionKey(option, index))
}

function selectOption(question, option, index) {
  const key = optionKey(option, index)
  if (question.type === 'MULTI_CHOICE') {
    const keys = selectedKeys(answers[question.id])
    const next = keys.includes(key) ? keys.filter(item => item !== key) : [...keys, key]
    handleAnswer(question.id, sortAnswerKeys(next))
    return
  }
  handleAnswer(question.id, key)
}

function isPracticeOptionSelected(option, index) {
  return selectedKeys(practiceAnswer.value).includes(optionKey(option, index))
}

function selectPracticeOption(option, index) {
  if (!practiceQuestion.value) return
  const key = optionKey(option, index)
  if (practiceQuestion.value.type === 'MULTI_CHOICE') {
    const keys = selectedKeys(practiceAnswer.value)
    const next = keys.includes(key) ? keys.filter(item => item !== key) : [...keys, key]
    practiceAnswer.value = sortAnswerKeys(next)
  } else {
    practiceAnswer.value = key
  }
  practiceResult.value = null
}

function startPractice(question) {
  practiceQuestion.value = question
  practiceAnswer.value = ''
  practiceResult.value = null
}

function checkPractice() {
  if (!practiceQuestion.value) return
  const ok = normalizePracticeAnswer(practiceAnswer.value) === normalizePracticeAnswer(practiceQuestion.value.correctAnswer)
  practiceResult.value = ok
}

function normalizePracticeAnswer(value) {
  return String(value || '').replace(/[\s,，、;；|]+/g, '').toLowerCase()
}

let saveDebounce = null
function handleAnswer(questionId, value) {
  answers[questionId] = value
  clearTimeout(saveDebounce)
  saveDebounce = setTimeout(() => saveAnswer(questionId, value), 250)
}

async function saveAnswer(questionId, value) {
  if (!currentExam.value) return
  await request(`/api/student/attempts/${currentExam.value.attemptId}/answer`, { method: 'POST', body: { questionId, answerText: value } })
}

async function saveAll() {
  if (!currentExam.value) return
  for (const q of currentQuestions.value) { await saveAnswer(q.id, answers[q.id] || '') }
  setMessage('已保存')
}

async function submitExam() {
  if (!currentExam.value) return
  try {
    const data = await request(`/api/student/attempts/${currentExam.value.attemptId}/submit`, { method: 'POST' })
    await finishAttempt(data)
    setMessage('交卷成功')
  } catch (e) { setMessage(e.message, 'error') }
}

async function finishAttempt(data) {
  clearInterval(timer)
  window.removeEventListener('blur', onBlur)
  currentExam.value = null
  currentQuestions.value = []
  currentIndex.value = 0
  currentSwitchCount.value = 0
  remainingSeconds.value = 0
  await loadStudent()
}

function closeExam() {
  currentExam.value = null
  currentQuestions.value = []
  currentIndex.value = 0
  currentSwitchCount.value = 0
  remainingSeconds.value = 0
  clearInterval(timer)
  window.removeEventListener('blur', onBlur)
}

watch(isDark, val => {
  document.documentElement.classList.toggle('dark', val)
})

watch(() => user.value, val => {
  if (val) localStorage.setItem('exam-user', JSON.stringify(val))
})

watch(
  [teacherQuestionFilter, () => ({ ...questionFilters })],
  () => {
    questionListPage.value = 1
  },
  { deep: true }
)

onMounted(async () => {
  if (window.matchMedia?.('(prefers-color-scheme: dark)').matches) {
    isDark.value = true
  }
  if (user.value) await loadAll()
})

onBeforeUnmount(() => {
  clearInterval(timer)
  window.removeEventListener('blur', onBlur)
})
</script>

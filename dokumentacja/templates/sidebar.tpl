<div id="header">
    <div id="logo">
        <h1>JSQL</h1>
        <span>.</span>
    </div>

    <div id="menu">
        <ul>
            {foreach from=$Dashboard item=foo name=foo}

            {assign var="sect" value=$foo.section scope=parent}

            <li {if isset($smarty.get.section) && $smarty.get.section == "$sect"}class="active"{/if}>
            	<a href="{if isset($foo.data)}#{else}?section={$foo.section}{/if}" {if isset($foo.data)}class="expand"{/if}>{$foo.name}</a>
            	{if isset($foo.data)}
            	<ul>
            		{foreach from=$foo.data item=sub}
            		<li {if isset($smarty.get.page) && $smarty.get.page == '$sub.page' && $smarty.get.section == '$foo.section'}class="active"{/if}>
            			<a href="?section={$foo.section}&page={$sub.page}">{$sub.name}</a>
            		</li>
            		{/foreach}
            	</ul>
            	{/if}
            </li>
            {/foreach}
        </ul>
    </div>
</div>